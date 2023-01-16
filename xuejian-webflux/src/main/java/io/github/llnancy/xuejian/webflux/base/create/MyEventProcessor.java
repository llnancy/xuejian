package io.github.llnancy.xuejian.webflux.base.create;

import java.util.ArrayList;
import java.util.List;

/**
 * my event processor
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public class MyEventProcessor {

    private final List<MyEventListener<?>> listeners = new ArrayList<>();

    public <T> void register(MyEventListener<T> eventListener) {
        System.out.println("event registered");
        listeners.add(eventListener);
    }
}
