package com.example.demo.service;

import com.example.demo.jwt.JwtTokenUtil;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.RetCode;
import com.example.demo.util.RetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/2
 * @Description：
 */
@Service
public class AuthService {

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtTokenUtil jwtTokenUtil;
    private UserMapper userMapper;


    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtTokenUtil jwtTokenUtil,
            UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userMapper = userMapper;
    }


    public RetResult login(Map<String,Object> map){
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(map.get("username").toString(), map.get("password").toString());
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(map.get("username").toString());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new RetResult(RetCode.SUCCESS.getCode(),token);

    }

}
