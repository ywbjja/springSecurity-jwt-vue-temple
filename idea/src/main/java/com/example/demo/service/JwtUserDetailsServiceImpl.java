package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.jwt.JwtUserFactory;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/2
 * @Description：
 */
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Map<String ,Object> map = new HashMap<String,Object>();
        map.put("user",s);
        User user = userMapper.queryByUserName(map);
        if(user == null){
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.",map.get("username")));
        }
        else {
            return JwtUserFactory.create(user);
        }
    }
}
