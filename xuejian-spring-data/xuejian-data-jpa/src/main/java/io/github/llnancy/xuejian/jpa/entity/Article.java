package io.github.llnancy.xuejian.jpa.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Article
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/6
 */
@Entity
@Table(name = "article")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Article implements Serializable {

    private static final long serialVersionUID = -2909625697734063374L;

    /**
     * {@link Id} 表示当前字段为主键
     * {@link GeneratedValue} 配置主键生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * {@link Column} 定义类属性对应的数据库表字段名，如果名称一致则可省略。
     */
    @Column(name = "title")
    private String title;

    private String author;

    @CreatedBy
    private String createUser;

    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedBy
    private String updateUser;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
