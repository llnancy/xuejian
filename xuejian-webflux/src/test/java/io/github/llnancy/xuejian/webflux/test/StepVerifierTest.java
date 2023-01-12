package io.github.llnancy.xuejian.webflux.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * test {@link StepVerifier}
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@SpringBootTest
public class StepVerifierTest {

    @Test
    public void test() {
        Flux<String> flux = Flux.just("hello", "webflux");

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
