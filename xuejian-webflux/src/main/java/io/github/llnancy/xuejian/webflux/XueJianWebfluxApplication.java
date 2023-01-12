package io.github.llnancy.xuejian.webflux;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * webflux application 启动器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2023/1/12
 */
@SpringBootApplication
public class XueJianWebfluxApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianWebfluxApplication.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}
