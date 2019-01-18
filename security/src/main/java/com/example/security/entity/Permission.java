package com.example.security.entity;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：
 */
@Data
public class Permission {


    private Integer per_id;
    private Integer per_parent_id;
    private String per_name;
    private String per_resource;
    private String per_type;
    private String per_icon;
    private String per_describe;
    private String per_component;
    private Integer per_sort;
    private List<Permission> children;

    public Permission() {
    }

    public Permission(Integer per_id, Integer per_parent_id, String per_name, String per_resource, String per_type, String per_icon, String per_describe, String per_component, Integer per_sort, List<Permission> children) {
        this.per_id = per_id;
        this.per_parent_id = per_parent_id;
        this.per_name = per_name;
        this.per_resource = per_resource;
        this.per_type = per_type;
        this.per_icon = per_icon;
        this.per_describe = per_describe;
        this.per_component = per_component;
        this.per_sort = per_sort;
        this.children = children;
    }
}
