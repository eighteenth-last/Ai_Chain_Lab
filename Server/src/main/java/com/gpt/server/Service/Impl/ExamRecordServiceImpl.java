package com.gpt.server.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpt.server.Entity.AnswerRecord;
import com.gpt.server.Entity.ExamRecord;
import com.gpt.server.Entity.Paper;
import com.gpt.server.Mapper.AnswerRecordMapper;
import com.gpt.server.Mapper.ExamRecordMapper;
import com.gpt.server.Service.ExamRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Service.PaperService;
import com.gpt.server.Vo.ExamRankingVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: ExamRecord服务实现类
 * @Version: 1.0
 */
@Slf4j
@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private PaperService paperService;

    @Autowired
    private AnswerRecordMapper answerRecordMapper;
    // 分页查询考试记录
    @Override
    public void pageExamRecords(Page<ExamRecord> examRecordPage, String studentName, Integer status, String startDate, String endDate) {
        LambdaQueryWrapper<ExamRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!ObjectUtils.isEmpty(studentName),ExamRecord::getStudentName, studentName);
        if(!ObjectUtils.isEmpty(status)){
            String strStatus=switch (status){
                case 0 -> "进行中";
                case 1 -> "已完成";
                case 2 -> "已批阅";
                default -> null;
            };
            queryWrapper.eq(ExamRecord::getStatus, strStatus);
        }
        queryWrapper.ge(!ObjectUtils.isEmpty(startDate),ExamRecord::getStartTime, startDate);  // 大于等于
        queryWrapper.le(!ObjectUtils.isEmpty(endDate),ExamRecord::getEndTime, endDate);  // 小于等于
        page(examRecordPage,queryWrapper);  // 对考试记录的单表查询
        if (ObjectUtils.isEmpty(examRecordPage.getRecords())){
            log.info("考试记录为空");
            return;
        }
        List<Integer> integerList = examRecordPage.getRecords().stream().map(ExamRecord::getExamId).toList();
        List<Paper> paperList = paperService.listByIds(integerList);
        Map<Long, Paper> paperMap = paperList.stream().collect(Collectors.toMap(Paper::getId, p -> p));
        examRecordPage.getRecords().forEach(examRecord -> {
            examRecord.setPaper(paperMap.get(examRecord.getExamId().longValue()));
        });
        log.info("考试记录分页查询成功，返回考试记录：{}", examRecordPage);

    }

    // 删除考试记录
    @Override
    public void removeExamRecord(Integer id) {
        ExamRecord examRecord = getById(id);
        if (examRecord == null){
            log.info("考试记录不存在");
            return;
        }
        if ("进行中".equals(examRecord.getStatus())){
            throw new RuntimeException("进行中的考试记录id=%s 不能删除".formatted(id));
        }
        removeById(id);
        answerRecordMapper.delete(new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId, id));
    }

    // 获取考试记录排行榜
    @Override
    public List<ExamRankingVo> rankList(Integer paperId, Integer limit) {
        List<ExamRankingVo> rankingVOs=examRecordMapper.queryRankList(paperId, limit);
        return rankingVOs;
    }
}