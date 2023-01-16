package io.github.llnancy.xuejian.jpa.entity;

import io.github.llnancy.xuejian.jpa.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 继承公共基类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "article")
public class ArticleEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8582688823913868880L;

    private String title;

    private String author;
}
