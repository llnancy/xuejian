package com.sunchaser.chunyu.pagehelper.repository.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Mybatis-Plus 用户表
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
@Data
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 地址
     */
    private String address;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 状态（0：正常；1：删除）
     */
    private Integer isDeleted;

}
