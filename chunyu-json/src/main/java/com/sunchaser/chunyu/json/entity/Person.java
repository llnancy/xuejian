package com.sunchaser.chunyu.json.entity;

// import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 实体类Person
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/3/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private String name;
    private Integer age;
    // @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private LocalDateTime createTime;
}
