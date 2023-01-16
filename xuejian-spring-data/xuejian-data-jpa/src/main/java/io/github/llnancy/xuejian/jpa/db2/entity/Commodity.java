package io.github.llnancy.xuejian.jpa.db2.entity;

import io.github.llnancy.xuejian.jpa.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * commodity
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Commodity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -443973861891229663L;

    private String name;
}
