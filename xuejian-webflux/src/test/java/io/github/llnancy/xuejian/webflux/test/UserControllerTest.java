package io.github.llnancy.xuejian.webflux.test;

import io.github.llnancy.xuejian.webflux.crud.controller.UserController;
import io.github.llnancy.xuejian.webflux.crud.entity.User;
import io.github.llnancy.xuejian.webflux.crud.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

/**
 * test {@link UserController}
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@WebFluxTest(value = UserController.class)
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    private static User user;

    @BeforeAll
    public static void beforeAll() {
        user = new User();
        user.setId(1L);
        user.setUsername("websocket");
        user.setPassword("123456");
    }

    @Test
    public void test() {
        BDDMockito.given(userService.save(user))
                .willReturn(Mono.just(1L));
        Long expect = webTestClient.post()
                .uri("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
        Assertions.assertNotNull(expect);
        Assertions.assertEquals(expect, 1L);
    }
}
