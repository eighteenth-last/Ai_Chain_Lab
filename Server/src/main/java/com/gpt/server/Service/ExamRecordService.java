package com.gpt.server.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpt.server.Entity.ExamRecord;
import com.gpt.server.Vo.ExamRankingVo;

import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: ExamRecord服务接口
 * @Version: 1.0
 */
public interface ExamRecordService extends IService<ExamRecord> {
    // 考试记录分页查询
    void pageExamRecords(Page<ExamRecord> examRecordPage, String studentName, Integer status, String startDate, String endDate);

    // 删除考试记录
    void removeExamRecord(Integer id);

    // 获取考试排行榜
    List<ExamRankingVo> rankList(Integer paperId, Integer limit);
}