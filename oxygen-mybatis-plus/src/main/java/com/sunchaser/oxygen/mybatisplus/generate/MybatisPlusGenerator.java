package com.sunchaser.oxygen.mybatisplus.generate;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;

import java.time.LocalDate;
import java.util.Collections;

import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.INTEGER;

/**
 * mybatis plus 3.5.1版本以上的代码生成器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/9
 */
public class MybatisPlusGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create(new DataSourceConfig
                        .Builder("jdbc:mysql://localhost:3306/oxygen_mp?useUnicode=true&characterEncoding=UTF-8&useSSL=false", "root", "123456")
                        .typeConvert(new MySqlTypeConvert() {
                            @Override
                            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                                IColumnType columnType = super.processTypeConvert(globalConfig, fieldType);
                                if (fieldType.contains("tinyint")) {
                                    columnType = INTEGER;
                                }
                                return columnType;
                            }
                        })// 自定义类型转换器：tinyint生成Integer替换默认的Boolean
                        .keyWordsHandler(new MySqlKeyWordsHandler()) // 处理数据库关键字
                )
                .globalConfig(builder -> {
                    builder.author("sunchaser admin@lilu.org.cn") // 设置类文件头部注释的作者
                            .fileOverride() // 覆盖已生成文件（即将过时）3.5.2版本不会进行覆盖
                            .commentDate(() -> "JDK8 " + LocalDate.now()) // 设置类文件头部注释的时间
                            .dateType(DateType.TIME_PACK) //使用Java8的新时间类型LocalDateTime
                            .outputDir("./sunchaser-oxygen/oxygen-mybatis-plus/src/main/java"); // 指定输出目录（相对or绝对路径均可）
                })
                .packageConfig(builder -> {
                    builder.parent("com.sunchaser.oxygen") // 设置父包名
                            .moduleName("mybatisplus") // 设置父包模块名
                            .entity("repository.entity") // entity包名
                            .mapper("repository.mapper") // mapper包名
                            .controller("web.controller") // controller包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "./sunchaser-oxygen/oxygen-mybatis-plus/src/main/resources/mapper")); // 指定xml文件生成的路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("mp_user") // 设置需要生成的表名
                            .entityBuilder()
                            .enableLombok() // 开启lombok
                            .logicDeleteColumnName("state") // 逻辑删除字段
                            // .superClass("com.sunchaser.shushan.wulingzhu.repository.entity.BaseEntity")
                            // .addSuperEntityColumns("id", "create_time", "update_time", "state")// 设置实体公共父类字段
                            .controllerBuilder()
                            .enableHyphenStyle() // 驼峰转连字符-
                            .enableRestStyle() // 使用@RestController
                            .serviceBuilder()
                            .formatServiceFileName("%sService");// service接口前面不带I
                })
                .templateConfig(builder -> {
                    builder.entity("/templates/entity.java") // 配置自定义的entity模板位置（不用带.ftl模板引擎后缀名），使用@Data注解
                            .mapper("/templates/mapper.java") // 自定义mapper模板位置，去掉默认的<p></p>标签
                            .service("/templates/service.java") // 自定义service模板位置，去掉默认的<p></p>标签
                            .serviceImpl("/templates/serviceImpl.java") // 自定义serviceImpl模板位置，去掉默认的<p></p>标签
                            .controller("/templates/controller.java"); // 自定义controller模板位置，去掉默认的<p></p>标签
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
