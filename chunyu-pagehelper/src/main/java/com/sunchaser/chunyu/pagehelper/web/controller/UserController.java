package com.sunchaser.chunyu.pagehelper.web.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sunchaser.chunyu.pagehelper.repository.entity.UserEntity;
import com.sunchaser.chunyu.pagehelper.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/17
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/users")
    public List<UserEntity> list(@RequestParam Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<UserEntity> userEntities = userMapper.selectList(Wrappers.emptyWrapper());
        Page<UserEntity> page = (Page<UserEntity>) userEntities;
        log.info("page: {}", page);
        return userEntities;
    }
}
