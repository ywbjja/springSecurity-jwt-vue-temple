package com.example.security.controller;

import com.example.security.service.RoleService;
import com.example.security.util.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/21
 * @Description：
 */
@Slf4j
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/getRoleList",method = RequestMethod.POST)
    public RetResult getRoleList(@RequestBody  Map<String,Object> map){
        return roleService.getRoleListByCond(map);
    }

    /**
     * 获取全部角色列表
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/getAllRoleList",method = RequestMethod.POST)
    public RetResult getAllRoleList(@RequestBody(required = false) Map<String, Object> map){
        return roleService.getAllRoleList(map);
    }

    /**
     * 根据权限id查询角色列表
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/getRoleListByPerId")
    public RetResult getRoleListByPerId(@RequestBody(required = false) Map<String, Object> map){
        return roleService.getRoleListByPerId(map);
    }
}
