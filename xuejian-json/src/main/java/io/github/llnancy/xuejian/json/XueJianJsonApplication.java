package io.github.llnancy.xuejian.json;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * SpringBoot 整合 JSON 序列化 启动器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/16
 */
@SpringBootApplication
public class XueJianJsonApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianJsonApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
