package com.gpt.server.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Entity.Question;
import com.gpt.server.Mapper.QuestionMapper;
import com.gpt.server.Service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Question服务实现类
 * @Version: 1.0
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    

} 