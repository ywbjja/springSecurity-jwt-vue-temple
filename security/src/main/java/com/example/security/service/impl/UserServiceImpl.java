package com.example.security.service.impl;

import com.example.security.entity.Permission;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.jwt.JwtTokenUtil;
import com.example.security.mapper.PermissionMapper;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private PermissionMapper permissionMapper;



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * @param passwordAES
     * @return
     * @throws AuthenticationException
     */
    @Override
    public RetResult login(String username, String passwordAES) throws AuthenticationException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = null;
        password = AesEncryptUtil.decrypt(passwordAES);
        if (username == null || passwordAES == null) {
            return new RetResult(RetCode.FAIL.getCode(),"用户名，密码不能为空");
        }
        if (! (userMapper.selectUserNameIsExist(username) > 0)){
            return new RetResult(RetCode.FAIL.getCode(),"用户名不存在，请重试");
        }else if(!encoder.matches(password,userMapper.selectPasswordByUsername(username))) {
            return new RetResult(RetCode.FAIL.getCode(), "密码输入不正确，请重试");
        }
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
                if(permission.getPer_type().equals("menu")){
                    permissions.add(new Permission(permission.getPer_id(),permission.getPer_parent_id(),permission.getPer_name(),
                            permission.getPer_resource(),permission.getPer_type(),permission.getPer_icon(),permission.getPer_describe(),
                            permission.getPer_component(),permission.getPer_sort(),permission.getPer_crtTime(), permission.getChildren()));
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
        List<Permission> permissions = new ArrayList<Permission>();
        Set<Role> roles = roleMapper.selectByUserName(username);
        roles.forEach( role -> {
            role.getPermissions().forEach( permission -> {
                if(permission.getPer_type().equals("menu")){
                    permissions.add(new Permission(permission.getPer_id(),permission.getPer_parent_id(),permission.getPer_name(),
                            permission.getPer_resource(),permission.getPer_type(),permission.getPer_icon(),permission.getPer_describe(),
                            permission.getPer_component(),permission.getPer_sort(),permission.getPer_crtTime(), permission.getChildren()));
                }
            });
        });

        return new RetResult(RetCode.SUCCESS.getCode(),"获取菜单树成功",GenTree.genRoot(permissions));
    }

    public Object getAllMenuTree(List<Permission> permissionList){
        List<Map<String,Object>> permissions = new ArrayList<>();
        permissionList.forEach( permission -> {
            if(permission != null) {
                List<Permission> permissionList1 = permissionMapper.getParentMenu(permission.getPer_id());
                //vue treeselect 需要的是{"id":"*","label":"*"}格式
                Map<String,Object> map1 = new HashMap<>();
                map1.put("id",permission.getPer_id());
                map1.put("label",permission.getPer_name());
                if( permissionList1 != null && permissionList1.size() > 0){
                    map1.put("children",getAllMenuTree(permissionList1));
                }
                permissions.add(map1);
            }
        });
        return permissions;
    }

    @Override
    public List<Permission> getMenuTreeByPid(Long per_parent_id) {
        return permissionMapper.getParentMenu(per_parent_id);
    }


}
