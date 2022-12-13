package io.github.llnancy.xuejian.graphql.kickstart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Address.java -> address.graphqls
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/5/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String province;
    private String city;
    private String area;
    private String detailAddress;
}
