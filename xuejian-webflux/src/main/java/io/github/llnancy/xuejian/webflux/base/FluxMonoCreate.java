package io.github.llnancy.xuejian.webflux.base;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * create Flux Mono
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/13
 */
public class FluxMonoCreate {

    public static void main(String[] args) {
        // 通过 Flux.just 静态方法枚举元素创建字符串序列
        Flux<String> seq1 = Flux.just("foo", "bar", "foobar");

        // 通过 Flux.fromIterable 静态方法从集合中创建字符串序列
        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
        Flux<String> seq2 = Flux.fromIterable(iterable);

        // 从数字 5 开始，生成 3 个元素的序列。
        Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3);

        // 0 个元素的 Mono
        Mono<String> noData = Mono.empty();

        // 1 个元素的 Mono
        Mono<String> data = Mono.just("foo");
    }
}
