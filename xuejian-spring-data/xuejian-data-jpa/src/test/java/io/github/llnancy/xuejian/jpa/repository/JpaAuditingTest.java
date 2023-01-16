package io.github.llnancy.xuejian.jpa.repository;

import io.github.llnancy.xuejian.jpa.entity.Article;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Test JPA Auditing
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/10
 */
@SpringBootTest
public class JpaAuditingTest {

    @Autowired
    private ArticleRepository articleRepository;

    @MockBean
    private AuditorAware<String> auditorAware;

    @BeforeEach
    public void before() {
        Mockito.when(auditorAware.getCurrentAuditor())
                .thenReturn(Optional.of("xuejian-jpa"));
    }

    @Test
    public void test() {
        Article article = new Article();
        article.setTitle("Spring Native");
        article.setAuthor("Spring");
        articleRepository.save(article);
        Assertions.assertEquals("xuejian-jpa", article.getCreateUser());
        Assertions.assertNotNull(article.getUpdateTime());
        System.out.println(article);
    }
}
