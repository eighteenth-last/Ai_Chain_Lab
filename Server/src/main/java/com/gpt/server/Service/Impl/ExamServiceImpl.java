package com.gpt.server.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Entity.ExamRecord;
import com.gpt.server.Mapper.ExamRecordMapper;
import com.gpt.server.Service.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Exam服务实现类
 * @Version: 1.0
 */
@Service
@Slf4j
public class ExamServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamService {

} 