package com.sunchaser.chunyu.druid;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sunchaser.chunyu.druid.repository.entity.MpUser;
import com.sunchaser.chunyu.druid.repository.mapper.MpUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/19
 */
@SpringBootApplication
@MapperScan(basePackages = "com.sunchaser.chunyu.druid.repository.mapper")
@RestController
@Slf4j
public class ChunYuDruidApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ChunYuDruidApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    @Autowired
    private MpUserMapper mpUserMapper;

    @GetMapping("/invoke")
    public void invoke() {
        List<MpUser> mpUserList = mpUserMapper.selectList(Wrappers.emptyWrapper());
        log.info("mpUserList={}", mpUserList);
    }
}
