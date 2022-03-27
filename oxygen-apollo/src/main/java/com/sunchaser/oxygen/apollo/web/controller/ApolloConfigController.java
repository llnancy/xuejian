package com.sunchaser.oxygen.apollo.web.controller;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import com.sunchaser.oxygen.apollo.config.OxygenApolloConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Apollo客户端读取配置项
 * 需添加VM Options启动参数：-Denv=DEV -Dapollo.meta=http://localhost:8080
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/27
 */
@RestController
public class ApolloConfigController {

    @ApolloConfig(value = "application.yml")
    private Config config;

    @GetMapping("/config")
    public String getConfig(String key) {
        return config.getProperty(key, null);
    }

    /**
     * yml format:
     * oxygen:
     * apollo:
     * jsonstr: '[{"name":"sun", "age": 18}, {"name":"chaser", "age": 20}]'
     */
    @ApolloJsonValue("${oxygen.apollo.jsonstr:[]}")
    private List<JsonBean> jsonBeanList;

    @Data
    public static class JsonBean {
        private String name;
        private Integer age;
    }

    @GetMapping("/json-value")
    public List<JsonBean> getJsonValue() {
        return jsonBeanList;
    }

    @Value("${oxygen.apollo.name:defaultVal}")
    private String key;
    @Value("#{'${oxygen.apollo.jsonstr:[]}'.split(',')}")
    private List<String> jsonList;

    @GetMapping("/placeholder")
    public void getConfigByPlaceholder() {
        System.out.println(key);
        System.out.println(jsonList);
    }

    @Autowired
    private OxygenApolloConfig oxygenApolloConfig;

    @GetMapping("/configurationProperties")
    public void getOxygenApolloConfig() {
        String name = oxygenApolloConfig.getName();
        List<String> jsonstr = oxygenApolloConfig.getJsonstr();
        System.out.println(name);
        System.out.println(jsonstr);
    }
}
