package com.gpt.server.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpt.server.Entity.Question;
import com.gpt.server.Vo.QuestionQueryVo;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Question服务接口
 * @Version: 1.0
 */
public interface QuestionService extends IService<Question> {

    // 查询问题列表分页
    void queryQuestionListPage(Page<Question> questionPage, QuestionQueryVo questionQueryVo);

    // java代码进行处理
    void queryQuestionListByStream(Page<Question> questionPage, QuestionQueryVo questionQueryVo);
}