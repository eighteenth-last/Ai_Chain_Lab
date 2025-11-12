package com.gpt.server.Mapper;

import com.gpt.server.Entity.Exam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Mapper
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Exam数据访问层接口
 * @Version: 1.0
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
    // 可以在这里添加自定义的查询方法
} 