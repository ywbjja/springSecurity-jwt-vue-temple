package com.example.security.controller;

import com.example.security.service.UserService;
import com.example.security.util.RetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.naming.Name;
import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/4
 * @Description：
 */
@RestController
public class AuthController {


    @Autowired
    private UserService userService;

    @RequestMapping(value = "${jwt.route.login}",method = RequestMethod.POST)
    public RetResult login(@RequestBody Map<String,Object> map){
        String username = map.get("username").toString();
        String password = map.get("password").toString();
        return userService.login(username,password);
    }
}
