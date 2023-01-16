package io.github.llnancy.xuejian.webflux.base.create;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Flux#create bridge listeners API
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public class FluxCreateBridge {

    public static void main(String[] args) {
        MyEventProcessor myEventProcessor = new MyEventProcessor();
        Flux<String> bridge = Flux.create(sink -> {
            myEventProcessor.register(
                    new MyEventListener<String>() {

                        @Override
                        public void onDataChunk(List<String> chunk) {
                            for (String s : chunk) {
                                // 数据块中的每一个元素都被桥接为 Flux 中的元素
                                sink.next(s);
                            }
                        }

                        @Override
                        public void processComplete() {
                            // 转化为 onComplete 事件
                            sink.complete();
                        }
                    }
            );
        });
        bridge.subscribe(System.out::println);
    }
}
