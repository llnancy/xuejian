package io.github.llnancy.xuejian.junit;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * junit 启动类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/19
 */
@SpringBootApplication
public class XueJianJunitApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianJunitApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
