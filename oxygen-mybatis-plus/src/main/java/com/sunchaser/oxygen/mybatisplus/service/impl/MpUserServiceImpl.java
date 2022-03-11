package com.sunchaser.oxygen.mybatisplus.service.impl;

import com.sunchaser.oxygen.mybatisplus.repository.entity.MpUser;
import com.sunchaser.oxygen.mybatisplus.repository.mapper.MpUserMapper;
import com.sunchaser.oxygen.mybatisplus.service.MpUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Mybatis-Plus 用户表 服务实现类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022-03-10
 */
@Service
public class MpUserServiceImpl extends ServiceImpl<MpUserMapper, MpUser> implements MpUserService {

}
