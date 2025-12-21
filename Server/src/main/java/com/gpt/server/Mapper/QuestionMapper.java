package com.gpt.server.Mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gpt.server.Entity.Question;
import com.gpt.server.Vo.QuestionQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Mapper
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Question数据访问层接口
 * @Version: 1.0
 */
public interface QuestionMapper extends BaseMapper<Question> {
    // 自定义方法
    @Select("SELECT category_id, COUNT(*) count FROM questions where is_deleted =0 GROUP BY category_id;")
    List<Map<String,Long>> selectQuestionCountCategory();

    // 定义一个查询方法，用mybatis-plus的分页插件
    // 返回必须为Ipage
    IPage<Question> selectQuestionPage(IPage<Question> page, @Param("questionQueryVo") QuestionQueryVo questionQueryVo);

    List<Question> selectQuestionByPaperId(Long paperId);
} 