package com.example.security.controller;

import com.example.security.service.UserService;
import com.example.security.util.RetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    public RetResult getMenuTree(@RequestBody Map<String,Object> map){
        //使用Spring Security 获取用户信息
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getMenuTree(userDetails.getUsername());
    }
}
