package io.github.llnancy.xuejian.rabbitmq.model;

import lombok.Data;

/**
 * 自定义实体消息
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/4/26
 */
@Data
public class MsgDTO /* implements Serializable */ {

    // private static final long serialVersionUID = -51410032238146012L;
    private String msg;
}
