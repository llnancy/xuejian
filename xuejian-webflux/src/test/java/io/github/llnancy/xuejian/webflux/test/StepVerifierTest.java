package io.github.llnancy.xuejian.webflux.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * {@link StepVerifier} test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@SpringBootTest
class StepVerifierTest {

    @Test
    public void test() {
        Flux<String> flux = Flux.just("hello", "webflux");

        // 初始化：将 Flux/Mono 数据流传入 StepVerifier 的 create 方法
        // 正常数据流断言：expectNext/expectNextMatches/assertNext
        // 完成数据流断言：expectComplete
        // 异常数据流断言：expectError/expectErrorMessage
        // 启动测试：verify 方法

        // expectNext
        StepVerifier.create(flux)
                .expectNext("hello")
                .expectNext("webflux")
                .expectComplete()
                .verify();

        // expectNextMatches
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextMatches(el -> el.equals("hello"))
                .expectNextMatches(el -> el.startsWith("web"))
                .expectComplete()
                .verify();

        // assertNext
        StepVerifier.create(flux)
                .expectSubscription()
                .assertNext(System.out::println)
                .assertNext(System.out::println)
                .expectComplete()
                .verify();

        // concatWith an exception
        flux = flux.concatWith(Mono.error(new IllegalArgumentException("illegal exception!")));

        // expectErrorMessage
        StepVerifier.create(flux)
                .expectNext("hello")
                .expectNext("webflux")
                .expectErrorMessage("illegal exception!")
                .verify();
    }
}
