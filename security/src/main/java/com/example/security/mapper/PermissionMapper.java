package com.example.security.mapper;

import com.example.security.entity.Permission;

import java.util.List;
import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/22
 * @Description：
 */
public interface PermissionMapper {
    List<Permission> getAllMenuTree();

    List<Permission> getParentMenu(Integer per_parent_id);

    Integer update(Map<String,Object> map);
}
