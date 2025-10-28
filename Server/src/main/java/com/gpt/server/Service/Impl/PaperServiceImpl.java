package com.gpt.server.Service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Entity.Paper;
import com.gpt.server.Mapper.PaperMapper;
import com.gpt.server.Service.PaperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 试卷服务实现类
 */
@Slf4j
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {


} 