package io.github.llnancy.xuejian.jpa.db1.entity;

import io.github.llnancy.xuejian.jpa.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * user
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7932126155512154584L;

    private String username;
}
