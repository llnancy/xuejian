# JPA

`JPA` 的全称是 `Java Persistence API`，即 `Java` 持久化 `API`，是 `SUN` 公司推出的一套 `ORM` 规范，发布在 `javax.persistence` 包下，注意不是 `ORM` 框架 —— 因为 `JPA` 只是提供 `API` 接口规范并未提供 `ORM` 实现。

# Spring Data JPA

`Spring Data JPA` 是 `Spring Data` 对 `JPA` 的封装，开发者仅需声明 `DAO` 层接口即可访问数据库。

核心依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

# 创建工程

创建 `Spring Boot` 项目 `xuejian-data-jpa`，完整 `pom.xml` 如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>io.github.llnancy</groupId>
        <artifactId>xuejian-spring-data</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>xuejian-data-jpa</artifactId>
    <description>使用 Spring Data JPA 实现单表 CRUD</description>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${springboot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

# 数据源及 JPA 配置

配置文件 `application.yml` 内容如下：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost/jpa_db?useUnicode=true&characterEncoding=utf-8
    username: root
    password: 123456
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

`spring.jpa.hibernate.ddl-auto` 配置项支持的值有：

- `create`：每次运行程序，如果没有数据表则新建表，表内所有数据会清空。
- `create-drop`：每次程序运行结束时清空表。
- `update`：每次运行程序，如果没有数据表则新建表，表内原有数据不会清空，只会更新。
- `validate`：运行程序会校验数据与数据库的字段类型是否相同，不同会报错。

# 创建实体类

```java
package io.github.llnancy.xuejian.jpa.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
```

`JPA` 提供的注解：

- `@Entity`：表示将实体类交给 `JPA` 管理，实体类的属性会和数据库表字段进行映射。
- `@Table`：指定数据库表名称。
- `@Id`：标识字段为数据库的主键列。
- `@GeneratedValue`：主键生成策略。取值为 `GenerationType` 枚举。
- `@Column`：定义类属性对应的数据库表字段名，如果名称一致则可省略。

`GenerationType` 枚举值：

- `TABLE`：通过表产生主键。框架借由表模拟序列产生主键，使用该策略可以使应用更易于数据库移植。
- `SEQUENCE`：通过序列产生主键。通过 `@SequenceGenerator` 注解指定序列名。`MySQL` 不支持这种方式。
- `IDENTITY`：采用数据库 `ID` 自增长的方式。一般用于 `MySQL` 数据库。
- `AUTO`：由 `JPA` 自动选择合适的策略。是默认选项。

# 创建 JPA 接口

```java
package io.github.llnancy.xuejian.jpa.repository;

import io.github.llnancy.xuejian.jpa.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * article repository
 * <p>
 * {@link JpaRepository<Article, Long>} 基本 CRUD 操作
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/6
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
```

继承 `JpaRepository<T, ID>` 接口即可，其中泛型参数 `T` 代表实体类型，泛型参数 `ID` 代表数据库表主键类型。

`ArticleRepository` 的类继承关系图如下：

![ArticleRepository](https://posts-cdn.lilu.org.cn/2023/01/ArticleRepository.png)

主要接口如下：

- `Repository`：顶层标记接口。无任何方法。
- `CrudRepository`：提供基本 `CRUD` 方法。
- `PagingAndSortingRepository`：提供分页和排序的方法。
- `QueryByExampleExecutor`：提供 `QueryByExample` 模式查询方法。
- `JpaRepository`：`JPA` 的扩展方法。

核心实现类：`SimpleJpaRepository`。

# CrudRepository

继承 `Repository` 接口，提供以下基本 `CRUD` 方法：

| method signature                            | description                                    |
| :------------------------------------------ | :--------------------------------------------- |
| save(S): S                                  | 保存单个实体。参数和返回结果可以是实体的子类。 |
| saveAll(Iterable<S>): Iterable<S>           | 批量保存                                       |
| findById(ID): Optional<T>                   | 根据主键查询实体，返回 Optional 对象。         |
| existsById(ID): boolean                     | 根据主键判断实体是否存在                       |
| findAll(): Iterable<T>                      | 查询实体的所有列表                             |
| findAllById(Iterable<ID>): Iterable<T>      | 根据主键列表查询实体列表                       |
| count(): long                               | 查询总数返回 long 类型                         |
| deleteById(ID): void                        | 根据主键删除                                   |
| delete(T): void                             | 根据实体进行删除                               |
| deleteAllById(Iterable<? extends ID>): void | 根据主键列表批量删除                           |
| deleteAll(Iterable<? extends T>): void      | 根据实体列表批量删除                           |
| deleteAll(): void                           | 删除所有                                       |

# PagingAndSortingRepository

继承 `CrudRepository` 接口，提供分页排序方法：

| method signature           | description                                                    |
| :------------------------- | :------------------------------------------------------------- |
| findAll(Sort): Iterable<T> | 根据排序参数 Sort 进行排序查询                                 |
| findAll(Pageable): Page<T> | 根据分页和排序参数 Pageable 进行查询，结果使用 Page 进行封装。 |

# JpaRepository

继承 `PagingAndSortingRepository` 和 `QueryByExampleExecutor` 接口，提供以下扩展方法：

| method signature                         | description                             |
| :--------------------------------------- | :-------------------------------------- |
| findAll(): List<T>                       | 重写自父接口 CrudRepository             |
| findAll(Sort): List<T>                   | 重写自父接口 PagingAndSortingRepository |
| findAllById(Iterable<ID>): List<T>       | 重写自父接口 CrudRepository             |
| saveAll(Iterable<S>): List<S>            | 重写自父接口 CrudRepository             |
| flush(): void                            | 刷新待处理的变更至数据库                |
| saveAndFlush(S): S                       | 保存一个实体，并即时刷新至数据库。      |
| saveAllAndFlush(Iterable<S>): List<S>    | 保存全部实体，并即时刷新至数据库。      |
| deleteAllInBatch(Iterable<T>): void      | 批量删除给定实体                        |
| deleteAllByIdInBatch(Iterable<ID>): void | 批量删除给定 ID 对应的实体              |
| deleteAllInBatch(): void                 | 批量删除所有实体                        |
| getById(ID): T                           | 根据 ID 查询单个实体                    |
| findAll(Example<S>): List<S>             | 重写自父接口 QueryByExampleExecutor     |
| findAll(Example<S>, Sort): List<S>       | 重写自父接口 QueryByExampleExecutor     |

# SimpleJpaRepository

关系型数据库的所有 `Repository` 接口的实现类都是 `SimpleJpaRepository`，内部代理了 `EntityManager` 进行实体操作。

# Defining Query Method（DQM）

`Spring Data JPA` 的一大特色是利用方法名和参数名来定义查询方法（`Defining Query Methods`）。具体的实现方式有两种：

1. 直接通过方法名定义实现。
2. 通过 `@Query` 注解手动在方法上定义。

## 方法名定义查询示例

语法：方法名由查询关键字 + 查询字段 + 限制性条件等组成。示例如下：

```java
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 根据 title 查询
     *
     * @param title title
     * @return list of {@link Article}
     */
    List<Article> findByTitle(String title);
}
```

方法名和参数名需要遵循以下规则：

- 方法名通常包含多个实体属性用于查询，属性之间可以使用 `AND` 和 `OR` 连接，也支持 `Between`、`LessThan`、`GreaterThan`、`Like` 等关键字。
- 方法名可以以 `findBy/readBy/getBy/queryBy/searchBy/streamBy/countBy/existsBy/deleteBy/removeBy` 开头。
- 查询结果可以排序，方法名包含 `OrderBy` + 属性 + `ASC`（`DESC`）。

更多示例：

```java
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 根据 title 模糊查询
     *
     * @param title title
     * @return list of {@link Article}
     */
    List<Article> findByTitleLike(String title);

    /**
     * 根据 title 和 author 模糊查询
     *
     * @param title  title
     * @param author author
     * @return list of {@link Article}
     */
    List<Article> findByTitleAndAuthor(String title, String author);

    /**
     * 根据 id 范围查询
     *
     * @param startId start id
     * @param endId   end id
     * @return list of {@link Article}
     */
    List<Article> findByIdBetween(Long startId, Long endId);

    /**
     * 查询 id 小于指定值
     *
     * @param id id
     * @return list of {@link Article}
     */
    List<Article> findByIdLessThan(Long id);

    /**
     * in 查询
     *
     * @param ids list of id
     * @return list of {@link Article}
     */
    List<Article> findByIdIn(List<Long> ids);

    /**
     * 查询时间在 createTime 之后的数据
     *
     * @param createTime create time
     * @return list of {@link Article}
     */
    List<Article> findByCreateTimeAfter(LocalDateTime createTime);

    /**
     * 分页查询，返回 {@link Page} 对象。
     * 默认会执行一条 count 的 SQL 语句。
     *
     * @param pageable {@link Pageable}
     * @param author   author
     * @return {@link Page}
     */
    Page<Article> findByAuthor(Pageable pageable, String author);

    /**
     * 分页查询，返回 {@link Slice} 对象。
     * 只知道是否有下一个 Slice 可用，不知道总 count，适用于不关心总共多少页的场景。
     *
     * @param author   author
     * @param pageable {@link Pageable}
     * @return {@link Slice}
     */
    Slice<Article> findByAuthor(String author, Pageable pageable);

    /**
     * 排序查询，返回实体集合。
     *
     * @param sort   {@link Sort}
     * @param author author
     * @return list of {@link Article}
     */
    List<Article> findByAuthor(Sort sort, String author);

    /**
     * 分页查询，返回实体集合。
     *
     * @param pageable {@link Pageable}
     * @param author   author
     * @return list of {@link Article}
     */
    // List<Article> findByAuthor(Pageable pageable, String author);

    /**
     * 查询第一条 First
     *
     * @return {@link Article}
     */
    Article findFirstByOrderByIdDesc();

    /**
     * 查询第一条 Top
     *
     * @return {@link Article}
     */
    Article findTopByOrderByIdAsc();

    /**
     * 查询 First3
     *
     * @param author author
     * @return list of {@link Article}
     */
    List<Article> findFirst3ByAuthor(String author);

    /**
     * 查询 Top3。
     * 当存在分页参数 {@link Pageable} 时，以 Top 后面的数字为准。
     * 返回值可以为 {@link Page} 对象，也可以直接用 {@link List} 。
     *
     * @param author   author
     * @param pageable {@link Pageable}
     * @return {@link Page}
     */
    Page<Article> findTop3ByAuthor(String author, Pageable pageable);

    /**
     * 查询 Top3，带有 distinct 关键字。
     *
     * @param author   author
     * @param pageable {@link Pageable}
     * @return {@link List}
     */
    List<Article> findDistinctArticleTop3ByAuthor(String author, Pageable pageable);
}
```

## 方法查询策略

默认的方法查询策略是 `QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND`。

查询策略枚举取值含义：

- `CREATE`：直接根据方法名进行创建，规则是根据方法名称的构造进行尝试，一般的方法是从方法名中删除给定的一组已知前缀，并解析该方法的其余部分。如果方法名不符合规则，启动的时候会报异常，这种情况可以理解为 `@Query` 配置无效。
- `USE_DECLARED_QUERY`：声明方式创建，启动的时候会尝试找到一个声明的查询，如果没有找到将抛出一个异常。可以理解为必须配置 `@Query`。
- `CREATE_IF_NOT_FOUND`：默认策略。先以声明方式（`@Query`）进行查找，如果没有找到与方法相匹配的查询，则用 `CREATE` 的方法名创建规则创建一个查询；当这两者都不满足的情况下，启动就会报错。

通常我们无需进行任何配置。特殊情况需要修改查询策略时可在启动类上添加：`@EnableJpaRepositories(queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND)` 进行修改。

## 查询关键字列表

| 关键字            | 示例                                     | 对应 JPQL 语句                                 |
| :---------------- | :--------------------------------------- | :--------------------------------------------- |
| And               | findByLastnameAndFirstname               | ... where x.lastname = ?1 and x.firstname = ?2 |
| Or                | findByLastnameOrFirstname                | ... where x.lastname = ?1 or x.firstname = ?2  |
| Is,Equals         | findByFirstnameIs, findByFirstnameEquals | ... where x.firstname = ?1                     |
| Between           | findByStartDateBetween                   | ... where x.startDate between ?1 and ?2        |
| LessThan          | findByAgeLessThan                        | ... where x.age < ?1                           |
| LessThanEqual     | findByAgeLessThanEqual                   | ... where x.age <= ?1                          |
| GreaterThan       | findByAgeGreaterThan                     | ... where x.age > ?1                           |
| GreaterThanEqual  | findByAgeGreaterThanEqual                | ... where x.age >= ?1                          |
| After             | findByStartDateAfter                     | ... where x.startDate > ?1                     |
| Before            | findByStartDateBefore                    | ... where x.startDate < ?1                     |
| IsNull            | findByAgeIsNull                          | ... where x.age is null                        |
| IsNotNull,NotNull | findByAge(Is)NotNull                     | ... where x.age not null                       |
| Like              | findByFirstnameLike                      | ... where x.firstname like ?1                  |
| NotLike           | findByFirstnameNotLike                   | ... where x.firstname not like ?1              |
| StartingWith      | findByFirstnameStartingWith              | ... where x.firstname like ?1 (参数增加前缀 %) |
| EndingWith        | findByFirstnameEndingWith                | ... where x.firstname like ?1 (参数增加后缀 %) |
| Containing        | findByFirstnameContaining                | ... where x.firstname like ?1 (参数被 % 包裹)  |
| OrderBy           | findByAgeOrderByLastnameDesc             | ... where x.age = ?1 order by x.lastname desc  |
| Not               | findByLastnameNot                        | ... where x.lastname <> ?1                     |
| In                | findByAgeIn(Collection ages)             | ... where x.age in ?1                          |
| NotIn             | findByAgeNotIn(Collection ages)          | ... where x.age not in ?1                      |
| True              | findByActiveTrue()                       | ... where x.active = true                      |
| False             | findByActiveFalse()                      | ... where x.active = false                     |
| IgnoreCase        | findByFirstnameIgnoreCase                | ... where UPPER(x.firstame) = UPPER(?1)        |

具体可查看 `org.springframework.data.repository.query.parser.Part.Type` 枚举类。

## @Query 注解方式查询

`@Query` 注解允许在方法上使用 `JPQL`。

`JPQL`：全称是 `Java Persistence Query Language`。`JPQL` 是 `JPA` 中定义的一种查询语言，它的写法十分类似于 `SQL` 语句，但是要把查询的表名换成实体类类名，把表中的字段名换成实体类的属性名。

使用示例：

```java
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 位置参数绑定
     * 错误写法：from Article where Article.author=?1 and Article.title=?2
     * 如果要使用类名点属性名需要使用别名
     * 正确写法：from Article as Article where Article.author=?1 and Article.title=?2
     *
     * @param author 参数一：author
     * @param title  参数二：title
     * @return list of {@link Article}
     */
    @Query("from Article as Article where Article.author=?1 and Article.title=?2")
    List<Article> findByQuery(String author, String title);

    /**
     * 命名参数
     *
     * @param title 命名参数 title
     * @return list of {@link Article}
     */
    @Query("from Article where title=:title")
    List<Article> findByQuery(@Param("title") String title);

    /**
     * like 模糊查询
     *
     * @param title title
     * @return list of {@link Article}
     */
    @Query("from Article as art where art.title like %:title%")
    List<Article> findByLikeQuery(@Param("title") String title);

    /**
     * order by 排序
     *
     * @param title title
     * @return list of {@link Article}
     */
    @Query("from Article as art where art.title like %:title% order by art.id desc")
    List<Article> findByOrderByQuery(@Param("title") String title);

    /**
     * 分页查询
     *
     * @param pageable {@link Pageable}
     * @param title    title
     * @return list of {@link Article}
     */
    @Query("from Article as art where art.title like %:title%")
    List<Article> findByPageQuery(Pageable pageable, @Param("title") String title);

    /**
     * in 查询
     *
     * @param ids collection of id
     * @return list of {@link Article}
     */
    @Query("from Article as art where art.id in :ids")
    List<Article> findByIdsQuery(@Param("ids") Collection<Long> ids);

    /**
     * 基于 SpEL 表达式的查询
     *
     * @param article {@link Article}
     * @return list of {@link Article}
     */
    @Query("from Article as art where art.author=:#{#article.author} and art.title=:#{#article.title}")
    List<Article> findBySpELQuery(@Param("article") Article article);

    /**
     * 使用原始 SQL：设置 nativeQuery = true
     *
     * @param title title
     * @return list of {@link Article}
     */
    @Query(value = "select * from article where title=:title", nativeQuery = true)
    List<Article> findByNativeQuery(@Param("title") String title);

    /*
     * 原始 SQL 不支持直接传递 Sort 参数，以下写法错误。
     *
     * @Query(value = "select * from article where title=:title", nativeQuery = true)
     * List<Article> findBySortQuery(@Param("title") String title, Sort sort);
     */

    /**
     * 原始 SQL 排序正确写法
     *
     * @param title title
     * @param sort  排序字段名（数据库表字段）
     * @return list of {@link Article}
     */
    @Query(value = "select * from article where title=:title order by :sort", nativeQuery = true)
    List<Article> findBySortQuery(@Param("title") String title, @Param("sort") String sort);

    /**
     * JPQL 排序
     *
     * @param title title
     * @param sort  {@link Sort}
     * @return list of {@link Article}
     */
    @Query("from Article as art where art.title=:title")
    List<Article> findBySortQuery(@Param("title") String title, Sort sort);

    /**
     * 原生 SQL 分页
     *
     * @param pageable {@link Pageable}
     * @param title    title
     * @return {@link Page}
     */
    @Query(value = "select * from article where title like %:title%",
            countQuery = "select count(1) from article where title=:title",
            nativeQuery = true
    )
    Page<Article> findByPageNativeQuery(Pageable pageable, @Param("title") String title);
}
```

# 常见 JPA 注解

所有的 `JPA` 注解都在 `javax.persistence` 包下。

## @Entity

作用在实体类上，表示将该类交给 `JPA` 进行管理，类的属性会和数据库表字段进行映射。

## @Table

用于指定数据库表名。非必填，默认表名和实体类名称一致。

## @Access

用于指定实体类中的注解是写在字段上还是 `getter/setter` 方法上。非必填，默认以实体类中第一个注解出现的位置为准。

## @Id

用于指定数据库主键。通常和 `@GeneratedValue` 注解配合使用。

## @GeneratedValue

主键生成策略。取值为 `GenerationType` 枚举。

## @Enumerated

枚举映射类型。取值为 `EnumType` 枚举。

```java
package javax.persistence;

public enum EnumType {

    /* 枚举值下标 */
    ORDINAL,

    /* 枚举值 name */
    STRING
}
```

## @Basic

表示该属性会映射成数据库的表字段。可省略。

```java
public @interface Basic {

    /**
     * FetchType：
     * EAGER：立即加载（默认）
     * LAZY：延迟加载（主要应用于大字段）
     */
    FetchType fetch() default EAGER;

    /**
     * 该字段是否可为 null。默认可以为 null。
     */
    boolean optional() default true;
}
```

## @Column

定义属性对应的数据库表列名。

```java
public @interface Column {

    /**
     * 数据库表列名。可不填，默认列名和实体属性名相同。
     */
    String name() default "";

    /**
     * 属性是否唯一
     */
    boolean unique() default false;

    /**
     * 属性是否可为空
     */
    boolean nullable() default true;

    /**
     * 执行 insert 操作时是否包含此属性
     */
    boolean insertable() default true;

    /**
     * 执行 update 操作时是否包含此属性
     */
    boolean updatable() default true;

    /**
     * 该属性在数据库表中的实际类型
     */
    String columnDefinition() default "";

    /**
     * 映射多个表时指定表名。默认为主表的表名。
     */
    String table() default "";

    /**
     * 数据库字符串类型列的长度
     */
    int length() default 255;

    /**
     * 当属性类型为 double 时，表示数值总长度。
     */
    int precision() default 0;

    /**
     * 当属性类型为 double 时，表示小数点所占位数。
     */
    int scale() default 0;
}
```

## @Transient

表示该属性不会映射成数据库表字段，即非持久化属性。和 `@Basic` 作用相反。

## @Temporal

用于设置 `java.util.Date` 类型的属性映射到对应精度的字段。有以下三种情况

- `@Temporal(TemporalType.DATE)`：映射成 `java.sql.Date`，仅有日期年月日。
- `@Temporal(TemporalType.TIME)`：映射成 `java.sql.Time`，仅有时间时分秒。
- `@Temporal(TemporalType.TIMESTAMP)`：映射成 `java.sql.Timestamp`，日期 + 时间。

## @MappedSuperclass

可实体类的公共属性抽取成父类，在父类上加 `@MappedSuperclass` 注解，子类进行继承。

# JPA 审计功能

在后台管理系统中，数据表通常会有以下四个字段：

- `createUser`：创建人。
- `createTime`：创建时间。
- `updateUser`：最后修改人。
- `updateTime`：最后修改时间。

`Spring Data JPA` 为这四个字段分别提供了相应注解：

- `@CreateBy`
- `@CreateDate`
- `@LastModifiedBy`
- `@LastModifiedDate`

## 使用示例

### 修改实体类

在实体类中添加四个字段，分别记录创建人、创建时间、最后修改人和最后修改时间。然后在实体类上添加 `@EntityListeners(AuditingEntityListener.class)` 注解。

```java
@Entity
@Table(name = "article")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Article implements Serializable {

    // 省略已有字段

    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedDate
    private LocalDateTime updateTime;

    @CreatedBy
    private String createUser;

    @LastModifiedBy
    private String updateUser;
}
```

### 实现 AuditorAware 接口

实现 `AuditorAware` 接口，获取当前操作用户。

实际开发中当前用户信息可能存放在 `Session` 或 `Redis` 中，如果使用了 `Spring Security` 框架，则当前用户信息存放在 `Security` 上下文中。所以 `AuditorAware` 接口的具体实现会有所差异，这里我们以 `Spring Security` 为例，获取当前操作用户：

```java
@Configuration
@EnableJpaAuditing
public class JpaConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditorAware<String> auditorAware() {
        // 以 Spring Security 为例
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            return Optional.ofNullable(((User) authentication.getPrincipal()).getUsername());
        };
    }
}
```

### @EnableJpaAuditing 开启 JPA 审计功能

在配置类 `JpaConfiguration` 上添加 `@EnableJpaAuditing` 注解，开启 `JPA` 审计功能。

## 测试

在测试代码中不给实体的四个审计字段设置值，当调用 `save` 方法保存后，实体的四个审计字段会被自动赋上相应值。

```java
@SpringBootTest
public class JpaAuditingTest {

    @Autowired
    private ArticleRepository articleRepository;

    @MockBean
    private AuditorAware<String> auditorAware;

    @Test
    public void test() {
        Mockito.when(auditorAware.getCurrentAuditor())
                .thenReturn(Optional.of("xuejian-jpa"));
        Article article = new Article();
        article.setTitle("Spring Native");
        article.setAuthor("Spring");
        articleRepository.save(article);
        Assertions.assertEquals("xuejian-jpa", article.getCreateUser());
        Assertions.assertNotNull(article.getUpdateTime());
        System.out.println(article);
    }
}
```

## 最佳实践

结合 `@MappedSuperclass` 注解，将主键 `id` 和审计字段抽取成基类，其它实体类只需继承即可。

基类 `BaseEntity`：

```java
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    /**
     * {@link Id} 表示当前字段为主键
     * {@link GeneratedValue} 配置主键生成策略
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 创建人
     */
    @CreatedBy
    private String createUser;

    /**
     * 创建时间
     */
    @CreatedDate
    private LocalDateTime createTime;

    /**
     * 最后修改人
     */
    @LastModifiedBy
    private String updateUser;

    /**
     * 最后更新时间
     */
    @LastModifiedDate
    private LocalDateTime updateTime;
}
```

子类进行继承：

```java
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "article")
public class ArticleEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8582688823913868880L;
    
    private String title;

    private String author;
}
```

> 注意：继承公共基类后子类最好重写 `equals` 和 `hashCode` 方法。如果使用的是 `lombok`，在子类上添加 `@EqualsAndHashCode(callSuper = true)` 注解。

# 多数据源配置

一般用于后台管理系统中，如果是微服务架构，不推荐使用多数据源。

我们以两个数据源为例，首先要做的是将不同数据源的实体类和 `Repository` 放置在不同包下，例如，将数据源 `1` 的实体类和 `Repository` 放置在 `io.github.llnancy.xuejian.jpa.db1` 包下；数据源 `2` 的实体类和 `Repository` 放置在 `io.github.llnancy.xuejian.jpa.db2` 包下。

下面是双数据源配置代码：

`DataSource1Config`：

```java
@Configuration
// 开启事务管理器
@EnableTransactionManagement
@EnableJpaRepositories(
        // 指定 datasource1 的 repository 包扫描路径
        basePackages = {"io.github.llnancy.xuejian.jpa.db1.repository"},
        // 指定 datasource1 的 EntityManagerFactory
        entityManagerFactoryRef = "db1EntityManagerFactory",
        // 指定 datasource1 的 TransactionManager
        transactionManagerRef = "db1TransactionManager"
)
public class DataSource1Config {

    @Bean
    @Primary
    // 指定 datasource1 的配置项前缀
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSourceProperties db1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    // 指定 datasource1 的 hikari 连接池配置项前缀
    @ConfigurationProperties(prefix = "spring.datasource.hikari.db1")
    public HikariDataSource db1DataSource(@Qualifier("db1DataSourceProperties") DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        String name = properties.getName();
        if (StringUtils.hasText(name)) {
            dataSource.setPoolName(name);
        }
        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean db1EntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                          @Qualifier("db1DataSource") DataSource dataSource,
                                                                          HibernateProperties hibernateProperties,
                                                                          JpaProperties jpaProperties) {
        return builder.dataSource(dataSource)
                // 指定 datasource1 的实体类路径
                .packages("io.github.llnancy.xuejian.jpa.db1.entity")
                .persistenceUnit("db1")
                .properties(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()))
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager db1TransactionManager(@Qualifier("db1EntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
```

`DataSource2Config`：

```java
@Configuration
// 开启事务管理器
@EnableTransactionManagement
@EnableJpaRepositories(
        // 指定 datasource2 的 repository 包扫描路径
        basePackages = {"io.github.llnancy.xuejian.jpa.db2.repository"},
        // 指定 datasource2 的 EntityManagerFactory
        entityManagerFactoryRef = "db2EntityManagerFactory",
        // 指定 datasource2 的 TransactionManager
        transactionManagerRef = "db2TransactionManager"
)
public class DataSource2Config {

    @Bean
    // 指定 datasource2 的配置项前缀
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSourceProperties db2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    // 指定 datasource2 的 hikari 连接池配置项前缀
    @ConfigurationProperties(prefix = "spring.datasource.hikari.db2")
    public HikariDataSource db2DataSource(@Qualifier("db2DataSourceProperties") DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        String name = properties.getName();
        if (StringUtils.hasText(name)) {
            dataSource.setPoolName(name);
        }
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean db2EntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                          @Qualifier("db2DataSource") DataSource dataSource,
                                                                          HibernateProperties hibernateProperties,
                                                                          JpaProperties jpaProperties) {
        return builder.dataSource(dataSource)
                // 指定 datasource2 的实体类路径
                .packages("io.github.llnancy.xuejian.jpa.db2.entity")
                .persistenceUnit("db2")
                .properties(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()))
                .build();
    }

    @Bean
    public PlatformTransactionManager db2TransactionManager(@Qualifier("db2EntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
```

`application.yml` 配置文件：

```yml
spring:
  datasource:
    # MySQL 数据源
    db1:
      url: jdbc:mysql://localhost/jpa_db?useUnicode=true&characterEncoding=utf-8
      username: root
      password: 123456
    # h2 数据源
    db2:
      url: jdbc:h2:~/test
      username: sa
      password: 123456
    hikari:
      # 多数据源连接池配置
      db1:
        pool-name: jpa-hikari-pool-db1
        maximum-pool-size: 8
      db2:
        pool-name: jpa-hikari-pool-db2
        maximum-pool-size: 4
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

> 注：使用 `h2` 数据库需添加依赖：
>
> ```xml
> <dependency>
>     <groupId>com.h2database</groupId>
>     <artifactId>h2</artifactId>
> </dependency>
> ```
