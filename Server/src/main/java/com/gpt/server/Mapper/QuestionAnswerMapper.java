package com.gpt.server.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gpt.server.Entity.QuestionAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Mapper
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: QuestionAnswer数据访问层接口
 * @Version: 1.0
 */
@Mapper
public interface QuestionAnswerMapper extends BaseMapper<QuestionAnswer> {
} 