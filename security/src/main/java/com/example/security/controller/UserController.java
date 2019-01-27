package com.example.security.controller;

import com.example.security.service.UserService;
import com.example.security.util.RetCode;
import com.example.security.util.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 通过token获取用户信息
     * @param map
     * @return
     */
    @RequestMapping(value = "/getUserInfo")
    public RetResult getUserInfo(@RequestBody(required = false) Map<String,Object>map){
        //使用Spring Security 获取用户信息
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info(userDetails.getUsername());
        return userService.getUserInfo(userDetails.getUsername());
    }


    /**
     * 获取当前用户下的路由菜单
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/queryMenuTree")
    public RetResult getMenuTree(@RequestBody(required = false) Map<String,Object> map){
        //使用Spring Security 获取用户信息
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info(userDetails.getUsername());
        return userService.getMenuTree(userDetails.getUsername());
    }


    /**
     * 获取所有的菜单并返回菜单树
     * @param map
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/getAllMenuTree")
    public RetResult getAllMenuTree(@RequestBody(required = false) Map<String,Object> map){
        //使用Spring Security 获取用户信息
        return new RetResult(RetCode.SUCCESS.getCode(),userService.getAllMenuTree(userService.getMenuTreeByPid(Long.parseLong("0"))));
    }



}
