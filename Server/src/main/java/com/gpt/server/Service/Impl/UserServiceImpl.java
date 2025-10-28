package com.gpt.server.Service.Impl;

import com.gpt.server.Entity.User;
import com.gpt.server.Mapper.UserMapper;
import com.gpt.server.Service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

} 