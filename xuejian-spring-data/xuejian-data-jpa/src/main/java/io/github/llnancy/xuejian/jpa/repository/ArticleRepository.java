package io.github.llnancy.xuejian.jpa.repository;

import io.github.llnancy.xuejian.jpa.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * article repository
 * <p>
 * {@link JpaRepository<Article, Long>} 基本 CRUD 操作
 * Spring Data JPA 在运行时会解析方法名称，自动生成对应查询语句。
 * 按照 Spring Data JPA 定义的规则，查询方法以 findBy 开头，涉及条件查询时，条件的属性用条件关键字连接。
 * 注意：条件属性首字母需大写。
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/6
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 根据 title 查询
     *
     * @param title title
     * @return list of {@link Article}
     */
    List<Article> findByTitle(String title);

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
     * @return {@link Page}
     */
    @Query("from Article as art where art.title like %:title%")
    Page<Article> findByPageQuery(Pageable pageable, @Param("title") String title);

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
