package io.github.llnancy.xuejian.webflux.base.push;

import java.util.ArrayList;
import java.util.List;

/**
 * my message processor
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public class MyMessageProcessor {

    private final List<MyMessageListener<?>> listeners = new ArrayList<>();

    private final List<?> history = new ArrayList<>();

    public <T> void register(MyMessageListener<T> messageListener) {
        System.out.println("message registered");
        listeners.add(messageListener);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getHistory(long n) {
        System.out.println("n = " + n);
        return (List<T>) history;
    }
}
