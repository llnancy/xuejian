package io.github.llnancy.xuejian.webflux.base;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 * Backpressure
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/15
 */
public class Backpressure {

    public static void main(String[] args) {
        Flux.range(1, 10)
                .doOnRequest(r -> System.out.println("request of " + r))
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println("Cancelling after having received " + value);
                        cancel();
                    }
                });
    }
}
