package com.sunchaser.chunyu.mybatisplus.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunchaser.mojian.base.util.JsonUtils;
import com.sunchaser.chunyu.mybatisplus.repository.entity.MpUser;
import com.sunchaser.chunyu.mybatisplus.service.MpUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mybatis-Plus 用户表 Controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022-03-10
 */
@RestController
@RequestMapping("/mp-user")
@Slf4j
public class MpUserController {
    @Autowired
    private MpUserService mpUserService;

    /* BaseMapper methods begin */

    /**
     * BaseMapper#insert返回自增主键
     */
    @PostMapping("/user")
    public Long insert(@RequestBody MpUser mpUser) {
        int affectedRows = mpUserService.getBaseMapper().insert(mpUser);
        log.info("affectedRows={}", affectedRows);
        return mpUser.getId();
    }

    /**
     * BaseMapper#deleteById根据ID删除
     */
    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable Long id) {
        int affectedRows = mpUserService.getBaseMapper().deleteById(id);
        log.info("affectedRows={}", affectedRows);
    }

    /**
     * BaseMapper#updateById根据ID更新
     */
    @PatchMapping("/user/{id}")
    public void update(@PathVariable Long id, @RequestBody MpUser mpUser) {
        mpUser.setId(id);
        int affectedRows = mpUserService.getBaseMapper().updateById(mpUser);
        log.info("affectedRows={}", affectedRows);
    }

    /**
     * BaseMapper#selectById根据ID查询
     */
    @GetMapping("/user/{id}")
    public MpUser selectById(@PathVariable Long id) {
        return mpUserService.getBaseMapper().selectById(id);
    }

    /**
     * BaseMapper#selectByIds根据ID集合批量查询
     */
    @GetMapping("/users")
    public List<MpUser> selectByIds(@RequestParam List<Long> ids) {
        return mpUserService.getBaseMapper().selectBatchIds(ids);
    }

    /**
     * BaseMapper#selectByMap根据columnMap多条件组合查询
     */
    @GetMapping("/users/selectByMap")
    public List<MpUser> selectByMap(@RequestParam Map<String, Object> columnMap) {
        return mpUserService.getBaseMapper().selectByMap(columnMap);
    }

    /**
     * BaseMapper#selectList条件构造器查询
     */
    @GetMapping("/users/selectList")
    public List<MpUser> selectList() {
        return mpUserService.getBaseMapper().selectList(null);
    }
    /* BaseMapper methods end */

    /* IService methods begin */

    /**
     * IService#count查询总记录数
     */
    @GetMapping("/count")
    public Long count() {
        return mpUserService.count();
    }

    /**
     * IService#saveBatch批量新增
     */
    @PostMapping("/users")
    public List<Long> batchInsert(@RequestBody List<MpUser> mpUserList) {
        boolean saveBatch = mpUserService.saveBatch(mpUserList);
        log.info("saveBatch={}", saveBatch);
        return mpUserList.stream().map(MpUser::getId).collect(Collectors.toList());
    }

    /**
     * IService#updateBatchById批量更新
     */
    @PatchMapping("/users")
    public void batchUpdate(@RequestBody List<MpUser> mpUserList) {
        boolean updateBatchById = mpUserService.updateBatchById(mpUserList);
        log.info("updateBatchById={}", updateBatchById);
    }

    /**
     * IService#removeBatchByIds根据ID集合批量删除
     */
    @DeleteMapping("/users")
    public void batchDelete(@RequestBody List<Long> idList) {
        boolean removeBatchByIds = mpUserService.removeBatchByIds(idList);
        log.info("removeBatchByIds={}", removeBatchByIds);
    }
    /* IService methods end */

    /* wrapper methods begin */

    /**
     * QueryWrapper查询条件构造器，使用字段名字符串。
     * 查询name包含"龙"，age在18~25之间，address不为null且按ID降序排序的user信息
     * 强烈建议使用LambdaQueryWrapper函数式，可以避免字段名拼写错误等问题。
     */
    @GetMapping("/wrapper/users")
    public List<MpUser> wrapperList() {
        QueryWrapper<MpUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "name", "age", "address") // 指定需要查询的字段
                .like("name", "龙")
                .between("age", 18, 25)
                .isNotNull("address")
                .orderByDesc("id");
        return mpUserService.list(queryWrapper);
    }

    /**
     * LambdaQueryWrapper函数式查询条件构造器
     * 查询name包含"龙"，age在18~25之间，address不为null且按ID降序排序的user信息
     */
    @GetMapping("/lambda-wrapper/users")
    public List<MpUser> lambdaWrapperList() {
        LambdaQueryWrapper<MpUser> lambdaQueryWrapper = new QueryWrapper<MpUser>().lambda()
                .select(MpUser::getId,
                        MpUser::getName,
                        MpUser::getAge,
                        MpUser::getAddress
                ) // 指定需要查询的字段
                .like(MpUser::getName, "龙")
                .between(MpUser::getAge, 18, 25)
                .isNotNull(MpUser::getAddress)
                .orderByDesc(MpUser::getId);
        return mpUserService.list(lambdaQueryWrapper);
    }

    /**
     * LambdaQueryWrapper实现删除
     * 输入参数id不为null时进行删除
     */
    @DeleteMapping("/lambda-wrapper/user/{id}")
    public Boolean lambdaWrapperDelete(@PathVariable Long id) {
        LambdaQueryWrapper<MpUser> lambdaQueryWrapper = new LambdaQueryWrapper<MpUser>()
                .eq(Objects.nonNull(id), MpUser::getId, id);
        return mpUserService.remove(lambdaQueryWrapper);
    }

    /**
     * LambdaUpdateWrapper实现修改
     * 可使用Wrappers.<MpUser>lambdaUpdate()静态方法代替new
     */
    @PatchMapping("/lambda-wrapper/users")
    public Boolean lambdaWrapperUpdate() {
        LambdaUpdateWrapper<MpUser> lambdaUpdateWrapper = Wrappers.<MpUser>lambdaUpdate()
                .eq(MpUser::getAge, 18)
                .isNotNull(MpUser::getAddress)
                .set(MpUser::getName, "年轻人");
        return mpUserService.update(lambdaUpdateWrapper);
    }
    /* wrapper methods end */

    /* page query */

    /**
     * BaseMapper#selectPage分页查询
     *
     * @param pageNo   当前页
     * @param pageSize 每页大小
     * @return 当前页数据
     */
    @GetMapping("/pageList")
    public List<MpUser> pageList(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Page<MpUser> page = new Page<>(pageNo, pageSize);
        Page<MpUser> mpUserPage = mpUserService.getBaseMapper().selectPage(page, Wrappers.emptyWrapper());
        log.info("mpUserPage={}", JsonUtils.toJsonString(mpUserPage));
        return mpUserPage.getRecords();
    }
}
