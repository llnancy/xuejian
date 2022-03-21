package com.sunchaser.oxygen.druid;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sunchaser.oxygen.druid.repository.mapper.MpUserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/19
 */
@SpringBootApplication
@MapperScan(basePackages = "com.sunchaser.oxygen.druid.repository.mapper")
@RestController
public class DruidApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DruidApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    @Autowired
    private MpUserMapper mpUserMapper;

    @GetMapping("/invoke")
    public void invoke() {
        System.out.println(mpUserMapper.selectList(Wrappers.emptyWrapper()));
    }
}
