package com.example.security.controller;

import com.example.security.service.PermissionService;
import com.example.security.util.RetCode;
import com.example.security.util.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/22
 * @Description：
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowCredentials= "true")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 更新菜单菜单
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(value = "/menus")
    public RetResult getRoleList(@RequestBody Map<String,Object> map){
        return permissionService.update(map);
    }

    /**
     * 添加菜单
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/menus")
    public RetResult addPermisson(@RequestBody Map<String,Object> map){
        return permissionService.add(map);
    }


    /**
     * 删除菜单
     * @param per_id
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping(value = "/menus/{per_id}")
    public RetResult delPermission(@PathVariable Long per_id){
        Map<String,Object> map = new HashMap<>();
        map.put("per_id",per_id);
        return permissionService.del(map);
    }

    /**
     * 获取所有菜单（树形表格数据）
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/getMenusTableTree")
    public RetResult getMenusTableTree(@RequestBody(required = false) Map<String,Object> map){
        //使用Spring Security 获取用户信息
        return new RetResult(RetCode.SUCCESS.getCode(),permissionService.queryAllMenusTree(map));
    }

    /**
     * 获取角色对应的id集合
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/getPerIdList")
    public RetResult getPerIdList(@RequestBody Map<String,Object> map){
        //使用Spring Security 获取用户信息
        return new RetResult(RetCode.SUCCESS.getCode(),permissionService.getPerIdList(map));
    }


    /**
     * 获取角色对应的id集合
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(value = "/perms")
    public RetResult addPerms(@RequestBody Map<String,Object> map){
        //使用Spring Security 获取用户信息
        return new RetResult(RetCode.SUCCESS.getCode(),permissionService.addRP(map));
    }
}
