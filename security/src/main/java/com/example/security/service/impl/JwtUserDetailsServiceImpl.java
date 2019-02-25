package com.example.security.service.impl;

import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.jwt.JwtUser;
import com.example.security.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/4
 * @Description：
 */
@Slf4j
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {



    @Autowired
    private UserMapper userMapper;
    @Override
    public JwtUser loadUserByUsername(String s) throws UsernameNotFoundException {

        User user = userMapper.selectByUserName(s);
        if(user == null){
            throw new UsernameNotFoundException(String.format("'%s'.这个用户不存在", s));
        }
        List<SimpleGrantedAuthority> collect = user.getRoles().stream().map(Role::getRolename).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new JwtUser(user.getId(),user.getUsername(), user.getPassword(), user.getState(), collect);
    }
}
