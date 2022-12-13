package io.github.llnancy.xuejian.pagehelper.repository.mapper;

import io.github.llnancy.xuejian.pagehelper.repository.entity.UserEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户表 Mapper 接口
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
public interface UserMapper {

    @Select("select * from mp_user")
    List<UserEntity> selectList();
}
