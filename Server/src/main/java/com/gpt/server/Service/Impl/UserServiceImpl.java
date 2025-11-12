package com.gpt.server.Service.Impl;

import com.gpt.server.Entity.User;
import com.gpt.server.Mapper.UserMapper;
import com.gpt.server.Service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: User服务实现类
 * @Version: 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

} 