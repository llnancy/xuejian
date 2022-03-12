DROP TABLE IF EXISTS `mp_user`;
CREATE TABLE `mp_user`
(
    `id`          bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        varchar(18) NOT NULL DEFAULT '无名氏' COMMENT '姓名',
    `age`         tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '年龄',
    `address`     varchar(64) NOT NULL DEFAULT '' COMMENT '地址',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `state`       tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '状态：0：正常；1：删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Mybatis-Plus 用户表';