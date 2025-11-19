package com.gpt.server.Mapper;


import com.gpt.server.Entity.QuestionChoice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Mapper
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: QuestionChoice数据访问层接口
 * @Version: 1.0
 */
public interface QuestionChoiceMapper extends BaseMapper<QuestionChoice> {
    //定义第二步查询方法
    @Select("select * from question_choices where question_id=#{questionId} and is_deleted = 0 order by sort asc;")
    List<QuestionChoice> selectListByQuestionId(Long questionId);
} 