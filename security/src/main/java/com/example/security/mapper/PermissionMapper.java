package com.example.security.mapper;

import com.example.security.entity.Permission;
import com.example.security.entity.RolePermission;

import java.util.List;
import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/22
 * @Description：
 */
public interface PermissionMapper {
    List<Permission> getAllMenuTree();

    List<Permission> getParentMenu(Long per_parent_id);


    Integer update(Map<String, Object> map);

    Integer add(Permission permission);

    List<String> getPerIdList(Long rp_role_id);

    Integer addRP(RolePermission rolePermission);

    Integer del(Long rp_role_id);

    Integer getCount(Long rp_role_id);
}