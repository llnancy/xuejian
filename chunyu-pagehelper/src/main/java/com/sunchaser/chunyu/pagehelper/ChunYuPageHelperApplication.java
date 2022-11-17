package com.sunchaser.chunyu.pagehelper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * page-helper 启动器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/17
 */
@SpringBootApplication
@MapperScan("com.sunchaser.chunyu.pagehelper.repository.mapper")
public class ChunYuPageHelperApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChunYuPageHelperApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
