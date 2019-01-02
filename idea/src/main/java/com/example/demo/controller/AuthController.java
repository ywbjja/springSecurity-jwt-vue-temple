package com.example.demo.controller;

import com.example.demo.jwt.JwtAuthenticationRequest;
import com.example.demo.service.AuthService;
import com.example.demo.util.RetCode;
import com.example.demo.util.RetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/2
 * @Description：
 */
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "${jwt.route.authentication.path}")
    public RetResult login( @RequestBody JwtAuthenticationRequest authenticationRequest){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("username",authenticationRequest.getUsername());
        map.put("password",authenticationRequest.getPassword());
        RetResult rr = authService.login(map);
        String token = (String) rr.getData();
        return new RetResult(RetCode.SUCCESS.getCode(),token);
    }



}
