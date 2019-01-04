package com.example.security.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/4
 * @Description：
 */
@Data
public class Role {

    private Integer id;

    private String describe;

    private String rolename;

    private Integer state;

    private Date upTime;

    private Date addTime;

    // 权限列表
}
