package com.gpt.server.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gpt.server.Entity.QuestionAnswer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionAnswerMapper extends BaseMapper<QuestionAnswer> {
} 