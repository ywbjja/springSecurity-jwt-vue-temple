package com.example.security.entity;

import lombok.Data;

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
    private Set<Permission> children;


}
