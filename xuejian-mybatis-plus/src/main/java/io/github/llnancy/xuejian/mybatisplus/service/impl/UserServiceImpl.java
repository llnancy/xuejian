package io.github.llnancy.xuejian.mybatisplus.service.impl;

import io.github.llnancy.xuejian.mybatisplus.repository.entity.UserEntity;
import io.github.llnancy.xuejian.mybatisplus.repository.mapper.UserMapper;
import io.github.llnancy.xuejian.mybatisplus.service.UserService;
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
