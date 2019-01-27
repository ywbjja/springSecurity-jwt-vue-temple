package com.example.security.mapper;

import com.example.security.entity.Permission;
import com.example.security.entity.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：
 */
public interface RoleMapper {

    Set<Role> selectByUserName(String username);

    ArrayList<Role> getRoleListByCond(Map<String,Object> map);

    ArrayList<Permission> getMenuTree(Map<String,Object> map);

    List<Role> getAllRoleList();

    List<Role> getRoleListByPerId(Long rp_per_id);
}
