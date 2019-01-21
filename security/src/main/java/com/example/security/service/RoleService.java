package com.example.security.service;

import com.example.security.entity.Role;
import com.example.security.util.RetResult;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：
 */
public interface RoleService {

    RetResult getRoleListByCond(Map<String,Object> map);
}
