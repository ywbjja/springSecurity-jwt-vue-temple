package com.example.security.controller;

import com.example.security.service.PermissionService;
import com.example.security.util.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/22
 * @Description：
 */
@Slf4j
@RestController
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(value = "/menus")
    public RetResult getRoleList(@RequestBody Map<String,Object> map){
        return permissionService.update(map);
    }
}
