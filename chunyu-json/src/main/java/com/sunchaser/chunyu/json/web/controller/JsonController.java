package com.sunchaser.chunyu.json.web.controller;

import com.sunchaser.chunyu.json.entity.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/16
 */
@RestController
public class JsonController {

    @GetMapping("/invoke")
    public List<Person> invoke() {
        List<Person> list = new ArrayList<>();
        Person p1 = new Person("p1", 18, new Date(), LocalDateTime.now());
        Person p2 = new Person("p2", 19, new Date(), LocalDateTime.now());
        Person p3 = new Person("p3", 20, new Date(), LocalDateTime.now());
        list.add(p1);
        list.add(p2);
        list.add(p3);
        return list;
    }
}
