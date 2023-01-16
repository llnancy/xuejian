package io.github.llnancy.xuejian.webflux.base.push;

import io.github.llnancy.xuejian.webflux.base.create.MyEventProcessor;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Flux#push bridge listeners API
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public class FluxPushBridge {

    public static void main(String[] args) {
        MyEventProcessor myEventProcessor = new MyEventProcessor();
        Flux<String> bridge = Flux.push(sink -> {
            myEventProcessor.register(
                    new SingleThreadEventListener<String>() {

                        @Override
                        public void onDataChunk(List<String> chunk) {
                            for (String s : chunk) {
                                sink.next(s);
                            }
                        }

                        @Override
                        public void processComplete() {
                            sink.complete();
                        }

                        @Override
                        public void processError(Throwable t) {
                            sink.error(t);
                        }
                    }
            );
        });
        bridge.subscribe(System.out::println);
    }
}
