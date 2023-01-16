package io.github.llnancy.xuejian.webflux.base.push;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * set up an OnRequest Consumer when Flux#create or Flux#push bridge listeners API
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public class FluxBridgeOnRequest {

    public static void main(String[] args) {
        MyMessageProcessor myMessageProcessor = new MyMessageProcessor();
        Flux<String> bridge = Flux.create(sink -> {
            myMessageProcessor.register(
                    new MyMessageListener<String>() {

                        @Override
                        public void onMessage(List<String> messages) {
                            for (String message : messages) {
                                sink.next(message);
                            }
                        }
                    }
            );
            sink.onRequest(n -> {
                List<String> messages = myMessageProcessor.getHistory(n);
                for (String message : messages) {
                    sink.next(message);
                }
            });
        });
        bridge.subscribe(System.out::println);
    }
}
