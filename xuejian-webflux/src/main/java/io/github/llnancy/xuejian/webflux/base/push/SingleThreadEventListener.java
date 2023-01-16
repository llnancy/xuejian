package io.github.llnancy.xuejian.webflux.base.push;

import io.github.llnancy.xuejian.webflux.base.create.MyEventListener;

import java.util.List;

/**
 * single-thread event listener
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/16
 */
public interface SingleThreadEventListener<T> extends MyEventListener<T> {

    @Override
    void onDataChunk(List<T> chunk);

    @Override
    void processComplete();

    void processError(Throwable t);
}
