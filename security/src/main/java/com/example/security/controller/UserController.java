package com.example.security.controller;

import com.example.security.service.UserService;
import com.example.security.util.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/queryMenuTree")
    public RetResult getMenuTree(@RequestBody(required = false) Map<String,Object> map){
        //使用Spring Security 获取用户信息
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info(userDetails.getUsername());
        return userService.getMenuTree(userDetails.getUsername());
    }
}
