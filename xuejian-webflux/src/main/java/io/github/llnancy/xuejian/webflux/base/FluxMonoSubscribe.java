package io.github.llnancy.xuejian.webflux.base;

import reactor.core.publisher.Flux;

/**
 * subscribe
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/13
 */
public class FluxMonoSubscribe {

    public static void main(String[] args) {
        // 生成 3 个元素的序列
        Flux<Integer> ints = Flux.range(1, 3);

        // 订阅
        ints.subscribe();

        // 订阅并对每一个元素处理
        ints.subscribe(System.out::println);

        // 生成 4 个元素的序列，并在获取第 4 个元素时触发错误。
        ints = Flux.range(1, 4)
                .map(i -> {
                    if (i <= 3) return i;
                    throw new RuntimeException("Got to 4");
                });

        // 订阅，对每一个元素处理，同时处理错误信号
        ints.subscribe(System.out::println, err -> System.out.println("Error: " + err));

        // 错误信号和完成信号都是终端事件，并且互斥（不会同时得到）。为了演示 completeConsumer，这里重新生成序列。
        ints = Flux.range(1, 4);

        // 订阅，对每一个元素处理，同时处理可能出现的错误信号，并且在序列成功完成时做处理。
        ints.subscribe(
                System.out::println,
                err -> System.out.println("Error: " + err),
                () -> System.out.println("Done")
        );
    }
}
