package com.gpt.server.Service;

import com.gpt.server.Entity.ExamRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpt.server.Vo.StartExamVo;
import com.gpt.server.Vo.SubmitAnswerVo;

import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Exam服务接口
 * @Version: 1.0
 */
public interface ExamService extends IService<ExamRecord> {
    // 开始考试
    ExamRecord startExam(StartExamVo startExamVo);

    // 获取考试记录
    ExamRecord getExamRecordById(Integer id);

    // 提交考试记录
    void submitExam(Integer examRecordId, List<SubmitAnswerVo> answers) throws InterruptedException;

    // ai智能判卷
    ExamRecord graderExam(Integer examRecordId) throws InterruptedException;
}
 