package io.github.llnancy.xuejian.webflux.base.push;

import java.util.List;

/**
 * my message listener
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public interface MyMessageListener<T> {

    void onMessage(List<T> messages);
}
