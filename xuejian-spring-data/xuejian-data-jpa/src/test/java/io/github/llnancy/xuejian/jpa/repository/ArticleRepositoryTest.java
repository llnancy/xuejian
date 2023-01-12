package io.github.llnancy.xuejian.jpa.repository;

import io.github.llnancy.xuejian.jpa.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Test {@link ArticleRepository}
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/9
 */
@SpringBootTest
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void save() {
        Article article = new Article();
        article.setTitle("Spring Data JPA");
        article.setAuthor("Spring");
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleRepository.save(article);
    }

    @Test
    public void update() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Spring-Data-JPA");
        article.setAuthor("Spring");
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        // 未设置值的字段会被更新为 null
        articleRepository.save(article);
    }

    @Test
    public void findById() {
        Optional<Article> article = articleRepository.findById(1L);
        System.out.println(article.orElse(null));
    }

    @Test
    public void findAll() {
        List<Article> articles = articleRepository.findAll();
        articles.forEach(System.out::println);
    }

    @Test
    public void deleteById() {
        articleRepository.deleteById(2L);
    }

    @Test
    public void findAllWithSort() {
        Sort sort = Sort.by(Sort.Order.desc("id"));
        List<Article> sortArticles = articleRepository.findAll(sort);
        sortArticles.forEach(System.out::println);
    }

    @Test
    public void findAllWithPage() {
        // page: 页码; size: 每页大小
        PageRequest request = PageRequest.of(0, 3);
        Page<Article> pageArticles = articleRepository.findAll(request);
        pageArticles.getContent().forEach(System.out::println);
    }

    @Test
    public void findAllWithPageAndSort() {
        Sort sort = Sort.by(Sort.Order.desc("id"));
        PageRequest request = PageRequest.of(0, 3, sort);
        Page<Article> pageAndSortArticles = articleRepository.findAll(request);
        pageAndSortArticles.getContent().forEach(System.out::println);
    }

    @Test
    void findByTitle() {
        List<Article> articles = articleRepository.findByTitle("Spring-Data-JPA");
        articles.forEach(System.out::println);
    }

    @Test
    void findByTitleLike() {
        List<Article> articles = articleRepository.findByTitleLike("JPA");
        articles.forEach(System.out::println);
    }

    @Test
    void findByTitleAndAuthor() {
        List<Article> articles = articleRepository.findByTitleAndAuthor("Spring-Data-JPA", "Spring");
        articles.forEach(System.out::println);
    }

    @Test
    void findByIdBetween() {
        List<Article> articles = articleRepository.findByIdBetween(0L, 10L);
        articles.forEach(System.out::println);
    }

    @Test
    void findByIdLessThan() {
        List<Article> articles = articleRepository.findByIdLessThan(10L);
        articles.forEach(System.out::println);
    }

    @Test
    void findByIdIn() {
        List<Article> articles = articleRepository.findByIdIn(Arrays.asList(1L, 2L, 3L));
        articles.forEach(System.out::println);
    }

    @Test
    void findByCreateTimeAfter() {
        List<Article> articles = articleRepository.findByCreateTimeAfter(LocalDateTime.now());
        articles.forEach(System.out::println);
    }

    @Test
    void findByAuthor() {
        Page<Article> page = articleRepository.findByAuthor(PageRequest.of(0, 3), "Spring");
        System.out.println(page.getTotalPages());
        page.getContent().forEach(System.out::println);
    }

    @Test
    void testFindByAuthor() {
        Slice<Article> slice = articleRepository.findByAuthor("Spring", PageRequest.of(0, 3));
        slice.getContent().forEach(System.out::println);
    }

    @Test
    void testFindByAuthor1() {
        List<Article> articles = articleRepository.findByAuthor(Sort.by(Sort.Order.desc("id")), "Spring");
        articles.forEach(System.out::println);
    }

    @Test
    void findFirstByOrderByIdDesc() {
        Article article = articleRepository.findFirstByOrderByIdDesc();
        System.out.println(article);
    }

    @Test
    void findTopByOrderByIdAsc() {
        Article article = articleRepository.findTopByOrderByIdAsc();
        System.out.println(article);
    }

    @Test
    void findFirst3ByAuthor() {
        List<Article> articles = articleRepository.findFirst3ByAuthor("Spring");
        articles.forEach(System.out::println);
    }

    @Test
    void findTop3ByAuthor() {
        Page<Article> articles = articleRepository.findTop3ByAuthor("Spring", PageRequest.of(0, 5));
        articles.getContent().forEach(System.out::println);
    }

    @Test
    void findDistinctArticleTop3ByAuthor() {
        List<Article> articles = articleRepository.findDistinctArticleTop3ByAuthor("Spring", PageRequest.of(0, 5));
        articles.forEach(System.out::println);
    }

    @Test
    void findByAuthorTitleQuery() {
        List<Article> articles = articleRepository.findByQuery("Spring", "Spring-Data-JPA");
        articles.forEach(System.out::println);
    }

    @Test
    void findByTitleQuery() {
        List<Article> articles = articleRepository.findByQuery("Spring-Data-JPA");
        articles.forEach(System.out::println);
    }

    @Test
    void findByLikeQuery() {
        List<Article> articles = articleRepository.findByLikeQuery("JPA");
        articles.forEach(System.out::println);
    }

    @Test
    void findByOrderByQuery() {
        List<Article> articles = articleRepository.findByOrderByQuery("JPA");
        articles.forEach(System.out::println);
    }

    @Test
    void findByPageQuery() {
        Page<Article> articles = articleRepository.findByPageQuery(PageRequest.of(0, 3), "JPA");
        articles.getContent().forEach(System.out::println);
    }

    @Test
    void findByIdsQuery() {
        List<Article> articles = articleRepository.findByIdsQuery(Arrays.asList(1L, 2L, 3L));
        articles.forEach(System.out::println);
    }

    @Test
    void findBySpELQuery() {
        Article article = articleRepository.findById(1L).orElse(null);
        List<Article> articles = articleRepository.findBySpELQuery(article);
        articles.forEach(System.out::println);
    }

    @Test
    void findByNativeQuery() {
        List<Article> articles = articleRepository.findByNativeQuery("Spring-Data-JPA");
        articles.forEach(System.out::println);
    }

    @Test
    void findBySortNativeQuery() {
        List<Article> articles = articleRepository.findBySortQuery("Spring-Data-JPA", "id");
        articles.forEach(System.out::println);
    }

    @Test
    void findBySortQuery() {
        List<Article> articles = articleRepository.findBySortQuery("Spring-Data-JPA", Sort.by("id"));
        articles.forEach(System.out::println);
        articles = articleRepository.findBySortQuery("Spring-Data-JPA", JpaSort.unsafe("length(title)"));
        articles.forEach(System.out::println);
    }

    @Test
    void findByPageNativeQuery() {
        Page<Article> page = articleRepository.findByPageNativeQuery(PageRequest.of(0, 3, Sort.by(Sort.Order.desc("id"))), "Spring");
        page.getContent().forEach(System.out::println);
    }
}