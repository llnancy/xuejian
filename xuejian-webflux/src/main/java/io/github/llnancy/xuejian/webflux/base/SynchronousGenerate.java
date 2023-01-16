package io.github.llnancy.xuejian.webflux.base;

import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

/**
 * synchronous generate
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public class SynchronousGenerate {

    public static void main(String[] args) {
        stateBasedGenerate();
        mutableStateGenerate();
        mutableStateGenerateWithConsumer();
    }

    private static void stateBasedGenerate() {
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3 * state);
                    if (state == 10) sink.complete();
                    return state + 1;
                }
        );
        flux.subscribe(System.out::println);
    }

    private static void mutableStateGenerate() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) sink.complete();
                    // 每次返回同一个实例作为新状态
                    return state;
                }
        );
        flux.subscribe(System.out::println);
    }

    private static void mutableStateGenerateWithConsumer() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) sink.complete();
                    return state;
                },
                (state) -> System.out.println("state: " + state)
        );
        flux.subscribe(System.out::println);
    }
}
