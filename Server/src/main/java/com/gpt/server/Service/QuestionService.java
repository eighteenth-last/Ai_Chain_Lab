package com.gpt.server.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpt.server.Entity.Question;
import com.gpt.server.Vo.QuestionQueryVo;

import java.util.List;

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
    // 查询题目详情
    Question queryQuestionById(Long id);

    // 创建题目并保存
    void saveQuestion(Question question);

    // 修改题目
    void updateQuestion(Long id, Question question);

    // 删除题目
    void removeQuestion(Long id);

    // 查询最热门的题目
    List<Question> queryPopularList(Integer size);
}