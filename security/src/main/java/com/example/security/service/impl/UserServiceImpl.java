package com.example.security.service.impl;

import com.example.security.entity.Permission;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.jwt.JwtTokenUtil;
import com.example.security.mapper.RoleMapper;
import com.example.security.mapper.UserMapper;
import com.example.security.service.UserService;
import com.example.security.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author:YangWenbin
 * @Description：
 * @Date:21:38 2019/1/5
 * @ModifiedBy:
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 获取用户信息
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        User user = userMapper.selectByUserName(username);
        log.info("userserviceimpl"+user);
        return user;
    }

    /**
     * 登录方法
     * @param username
     * @param password
     * @return
     * @throws AuthenticationException
     */
    @Override
    public RetResult login(String username, String password) throws AuthenticationException {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new RetResult(RetCode.SUCCESS.getCode(),"登录成功",jwtTokenUtil.generateToken(userDetails));
    }

    /**
     * 获取用户信息，包括角色列表，权限资源，返回给前端使用
     * @param username
     * @return
     */
    @Override
    public RetResult getUserInfo(String username) {
        User user = userMapper.selectByUserName(username);
        //查询出改用户下的角色和权限
        Set<Role> roles = roleMapper.selectByUserName(username);
        List<Permission> permissions = new ArrayList<Permission>();
        Set<Menu> menus = new HashSet<>();
        roles.forEach( role -> {
            role.getPermissions().forEach( permission -> {
                //这里应该type为menu的，button目前还未添加
                if(permission.getPer_type().equals("menu")){
                    permissions.add(new Permission(permission.getPer_id(),permission.getPer_parent_id(),permission.getPer_name(),
                            permission.getPer_resource(),permission.getPer_type(),permission.getPer_icon(),permission.getPer_describe(),
                            permission.getPer_component(),permission.getPer_sort(),permission.getChildren()));
                }
            });
        });
        UserVo userVo =new UserVo(user.getId(),user.getUsername(),user.getRoles(), BulidTree.genRoot(GenTree.genRoot(permissions)));
        return new RetResult(RetCode.SUCCESS.getCode(),userVo);

    }

    /**
     * 获取菜单树
     * @param username
     * @return
     */
    @Override
    public RetResult getMenuTree(String username) {

        return new RetResult(RetCode.SUCCESS.getCode(),"获取菜单树成功");
    }


}
