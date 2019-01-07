package com.example.security.mapper;

import com.example.security.entity.Role;

import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：
 */
public interface RoleMapper {
    Set<Role> selectByUserName(String username);
}
