package io.github.llnancy.xuejian.webflux.base;

import lombok.NonNull;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

/**
 * BaseSubscriber
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/13
 */
public class BaseSubscriberExample {

    public static void main(String[] args) {
        SampleSubscriber<Integer> ss = new SampleSubscriber<>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(ss);
    }

    public static class SampleSubscriber<T> extends reactor.core.publisher.BaseSubscriber<T> {

        @Override
        protected void hookOnSubscribe(@NonNull Subscription subscription) {
            System.out.println("Subscribed");
            request(1);
        }

        @Override
        protected void hookOnNext(@NonNull T value) {
            System.out.println(value);
            request(1);
        }
    }
}
