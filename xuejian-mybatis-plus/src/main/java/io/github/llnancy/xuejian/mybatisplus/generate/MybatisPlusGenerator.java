package io.github.llnancy.xuejian.mybatisplus.generate;

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
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static com.baomidou.mybatisplus.generator.config.rules.DbColumnType.INTEGER;

/**
 * mybatis plus 3.5.2 版本以上的代码生成器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/9
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create(new DataSourceConfig
                        .Builder("jdbc:mysql://localhost:3306/xuejian_mp?useUnicode=true&characterEncoding=UTF-8&useSSL=false", "root", "123456")
                        // 自定义类型转换器：tinyint 生成 Integer 替换默认的 Boolean
                        .typeConvert(new MySqlTypeConvert() {
                            @Override
                            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                                IColumnType columnType = super.processTypeConvert(globalConfig, fieldType);
                                if (fieldType.contains("tinyint")) {
                                    columnType = INTEGER;
                                }
                                return columnType;
                            }
                        })
                        // 处理数据库关键字
                        .keyWordsHandler(new MySqlKeyWordsHandler())
                )
                .globalConfig(builder -> {
                    builder.author("sunchaser admin@lilu.org.cn") // 设置类文件头部注释的作者
                            .fileOverride() // 覆盖已生成文件（即将过时）3.5.2 版本不会进行覆盖
                            .commentDate(() -> "JDK8 " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))) // 设置类文件头部注释的时间
                            .dateType(DateType.TIME_PACK) // 使用 Java8 的新时间类型 LocalDateTime
                            .outputDir("./xuejian-mybatis-plus/src/main/java"); // 指定输出目录（相对 or 绝对路径均可）
                })
                .packageConfig(builder -> {
                    builder.parent("io.github.llnancy.xuejian") // 设置父包名
                            .moduleName("mybatisplus") // 设置父包模块名
                            .entity("repository.entity") // entity 包名
                            .mapper("repository.mapper") // mapper 包名
                            .controller("web.controller") // controller 包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "./xuejian-mybatis-plus/src/main/resources/mapper")); // 指定 xml 文件生成的路径
                })
                .strategyConfig(builder -> {
                    builder.addTablePrefix("mp_") // 增加过滤表前缀
                            .entityBuilder() // Entity 策略配置
                            .enableLombok() // 开启 lombok
                            .formatFileName("%sEntity") // 实体类以 Entity 结尾
                            .logicDeleteColumnName("is_deleted") // 逻辑删除字段
                            // .superClass("com.sunchaser.shushan.wulingzhu.repository.entity.BaseEntity")
                            // .addSuperEntityColumns("id", "create_time", "update_time", "is_deleted")// 设置实体公共父类字段
                            .controllerBuilder() // Controller 策略配置
                            .enableHyphenStyle() // 驼峰转连字符 -
                            .enableRestStyle() // 使用 @RestController
                            .serviceBuilder() // Service 策略配置
                            .formatServiceFileName("%sService");// service 接口以 Service 结尾
                })
                .templateConfig(builder -> {
                    builder.entity("/templates/entity.java") // 配置自定义的 entity 模板位置（不用带 .ftl 模板引擎后缀名），使用 @Data 注解
                            .mapper("/templates/mapper.java") // 自定义 mapper 模板位置，去掉默认的 <p></p> 标签
                            .service("/templates/service.java") // 自定义 service 模板位置，去掉默认的 <p></p> 标签
                            .serviceImpl("/templates/serviceImpl.java") // 自定义 serviceImpl 模板位置，去掉默认的 <p></p> 标签
                            .controller("/templates/controller.java"); // 自定义 controller 模板位置，去掉默认的 <p></p> 标签
                })
                // 使用 Freemarker 引擎模板，默认的是 Velocity 引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
