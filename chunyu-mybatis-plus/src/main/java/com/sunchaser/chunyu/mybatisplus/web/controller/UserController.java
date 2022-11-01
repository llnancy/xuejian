package com.sunchaser.chunyu.mybatisplus.web.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunchaser.chunyu.mybatisplus.repository.entity.UserEntity;
import com.sunchaser.chunyu.mybatisplus.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mybatis-Plus 用户表 Controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/11/01
 */
@RestController
@RequestMapping("/user-entity")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /* BaseMapper methods begin */

    /**
     * BaseMapper#insert：返回自增主键
     */
    @PostMapping("/user")
    public Long insert(@RequestBody UserEntity userEntity) {
        int affectedRows = userService.getBaseMapper().insert(userEntity);
        log.info("affectedRows={}", affectedRows);
        return userEntity.getId();
    }

    /**
     * BaseMapper#deleteById：根据 ID 删除
     */
    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable Long id) {
        int affectedRows = userService.getBaseMapper().deleteById(id);
        log.info("affectedRows={}", affectedRows);
    }

    /**
     * BaseMapper#updateById：根据 ID 更新
     */
    @PatchMapping("/user/{id}")
    public void update(@PathVariable Long id, @RequestBody UserEntity userEntity) {
        userEntity.setId(id);
        int affectedRows = userService.getBaseMapper().updateById(userEntity);
        log.info("affectedRows={}", affectedRows);
    }

    /**
     * BaseMapper#selectById：根据 ID 查询
     */
    @GetMapping("/user/{id}")
    public UserEntity selectById(@PathVariable Long id) {
        return userService.getBaseMapper().selectById(id);
    }

    /**
     * BaseMapper#selectByIds：根据 ID 集合批量查询
     */
    @GetMapping("/users")
    public List<UserEntity> selectByIds(@RequestParam List<Long> ids) {
        return userService.getBaseMapper().selectBatchIds(ids);
    }

    /**
     * BaseMapper#selectByMap：根据 columnMap 多条件组合查询
     */
    @GetMapping("/users/selectByMap")
    public List<UserEntity> selectByMap(@RequestParam Map<String, Object> columnMap) {
        return userService.getBaseMapper().selectByMap(columnMap);
    }

    /**
     * BaseMapper#selectList：条件构造器查询
     */
    @GetMapping("/users/selectList")
    public List<UserEntity> selectList() {
        return userService.getBaseMapper().selectList(null);
    }
    /* BaseMapper methods end */

    /* IService methods begin */

    /**
     * IService#count：查询总记录数
     */
    @GetMapping("/count")
    public Long count() {
        return userService.count();
    }

    /**
     * IService#saveBatch：批量新增
     */
    @PostMapping("/users")
    public List<Long> batchInsert(@RequestBody List<UserEntity> userEntityList) {
        boolean saveBatch = userService.saveBatch(userEntityList);
        log.info("saveBatch={}", saveBatch);
        return userEntityList.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
    }

    /**
     * IService#updateBatchById：批量更新
     */
    @PatchMapping("/users")
    public void batchUpdate(@RequestBody List<UserEntity> userEntityList) {
        boolean updateBatchById = userService.updateBatchById(userEntityList);
        log.info("updateBatchById={}", updateBatchById);
    }

    /**
     * IService#removeBatchByIds：根据 ID 集合批量删除
     */
    @DeleteMapping("/users")
    public void batchDelete(@RequestBody List<Long> idList) {
        boolean removeBatchByIds = userService.removeBatchByIds(idList);
        log.info("removeBatchByIds={}", removeBatchByIds);
    }
    /* IService methods end */

    /* wrapper methods begin */

    /**
     * QueryWrapper 查询条件构造器，使用字段名字符串。
     * 查询 name 包含"龙"，age 在 18~25 之间，address 不为 null 且按 ID 降序排序的 user 信息
     * 强烈建议使用 LambdaQueryWrapper 函数式，可以避免字段名拼写错误等问题。
     */
    @GetMapping("/wrapper/users")
    public List<UserEntity> wrapperList() {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        // select：指定需要查询的字段
        queryWrapper.select("id", "name", "age", "address")
                .like("name", "龙")
                .between("age", 18, 25)
                .isNotNull("address")
                .orderByDesc("id");
        return userService.list(queryWrapper);
    }

    /**
     * LambdaQueryWrapper 函数式查询条件构造器
     * 查询 name 包含"龙"，age 在 18~25 之间，address 不为 null 且按 ID 降序排序的 user 信息
     */
    @GetMapping("/lambda-wrapper/users")
    public List<UserEntity> lambdaWrapperList() {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new QueryWrapper<UserEntity>().lambda()
                .select(UserEntity::getId,
                        UserEntity::getName,
                        UserEntity::getAge,
                        UserEntity::getAddress
                ) // 指定需要查询的字段
                .like(UserEntity::getName, "龙")
                .between(UserEntity::getAge, 18, 25)
                .isNotNull(UserEntity::getAddress)
                .orderByDesc(UserEntity::getId);
        return userService.list(lambdaQueryWrapper);
    }

    /**
     * LambdaQueryWrapper 实现删除
     * 输入参数 id 不为 null 时进行删除
     */
    @DeleteMapping("/lambda-wrapper/user/{id}")
    public Boolean lambdaWrapperDelete(@PathVariable Long id) {
        LambdaQueryWrapper<UserEntity> lambdaQueryWrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(Objects.nonNull(id), UserEntity::getId, id);
        return userService.remove(lambdaQueryWrapper);
    }

    /**
     * LambdaUpdateWrapper 实现修改
     * 可使用 Wrappers.<UserEntity>lambdaUpdate() 静态方法代替 new
     */
    @PatchMapping("/lambda-wrapper/users")
    public Boolean lambdaWrapperUpdate() {
        LambdaUpdateWrapper<UserEntity> lambdaUpdateWrapper = Wrappers.<UserEntity>lambdaUpdate()
                .eq(UserEntity::getAge, 18)
                .isNotNull(UserEntity::getAddress)
                .set(UserEntity::getName, "年轻人");
        return userService.update(lambdaUpdateWrapper);
    }
    /* wrapper methods end */

    /* page query */

    /**
     * BaseMapper#selectPage：分页查询
     *
     * @param pageNo   当前页
     * @param pageSize 每页大小
     * @return 当前页数据
     */
    @GetMapping("/pageList")
    public List<UserEntity> pageList(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Page<UserEntity> page = new Page<>(pageNo, pageSize);
        Page<UserEntity> userEntityPage = userService.getBaseMapper().selectPage(page, Wrappers.emptyWrapper());
        log.info("userEntityPage={}", JSONUtil.toJsonStr(userEntityPage));
        return userEntityPage.getRecords();
    }
}
