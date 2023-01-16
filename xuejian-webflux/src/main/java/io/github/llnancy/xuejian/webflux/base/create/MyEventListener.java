package io.github.llnancy.xuejian.webflux.base.create;

import java.util.List;

/**
 * event listener
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public interface MyEventListener<T> {

    void onDataChunk(List<T> chunk);

    void processComplete();
}
