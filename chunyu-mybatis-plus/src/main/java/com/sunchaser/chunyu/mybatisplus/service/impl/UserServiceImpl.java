package com.sunchaser.chunyu.mybatisplus.service.impl;

import com.sunchaser.chunyu.mybatisplus.repository.entity.UserEntity;
import com.sunchaser.chunyu.mybatisplus.repository.mapper.UserMapper;
import com.sunchaser.chunyu.mybatisplus.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Mybatis-Plus 用户表 服务实现类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

}
