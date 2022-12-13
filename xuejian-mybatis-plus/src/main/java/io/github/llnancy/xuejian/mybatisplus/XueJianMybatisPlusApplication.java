package io.github.llnancy.xuejian.mybatisplus;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * mybatis-plus 启动器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/10
 */
@SpringBootApplication
public class XueJianMybatisPlusApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XueJianMybatisPlusApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
