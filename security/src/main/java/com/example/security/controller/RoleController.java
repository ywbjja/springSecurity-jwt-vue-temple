package com.example.security.controller;

import com.example.security.service.RoleService;
import com.example.security.util.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/getRoleList",method = RequestMethod.POST)
    public RetResult getRoleList(@RequestBody  Map<String,Object> map){
        return roleService.getRoleListByCond(map);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/getAllRoleList",method = RequestMethod.POST)
    public RetResult getAllRoleList(@RequestBody(required = false) Map<String, Object> map){
        return roleService.getAllRoleList(map);
    }
}
