package com.example.security.service;

import com.example.security.entity.Permission;
import com.example.security.util.RetResult;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/22
 * @Description：
 */
public interface PermissionService {
    public RetResult update(Map<String,Object> map);
}
