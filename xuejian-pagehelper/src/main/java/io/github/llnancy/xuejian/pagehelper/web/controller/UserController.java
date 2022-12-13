package io.github.llnancy.xuejian.pagehelper.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.github.llnancy.xuejian.pagehelper.repository.entity.UserEntity;
import io.github.llnancy.xuejian.pagehelper.repository.mapper.UserMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * user controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/17
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/users")
    public List<UserEntity> list(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<UserEntity> userEntities = userMapper.selectList();
        Page<UserEntity> page = (Page<UserEntity>) userEntities;
        log.info("page: {}", page);
        return userEntities;
    }

    @GetMapping("/users-2")
    public List<UserEntity> list2(PageBean pageBean) {
        PageHelper.startPage(pageBean);
        List<UserEntity> userEntities = userMapper.selectList();
        Page<UserEntity> page = (Page<UserEntity>) userEntities;
        log.info("page: {}", page);
        return userEntities;
    }

    @Data
    static class PageBean {

        /**
         * 当前页数
         * 对应配置项 pagehelper.params: pageNum=pageNo
         */
        private Integer pageNo;

        /**
         * 每页数量
         */
        private Integer pageSize;
    }
}
