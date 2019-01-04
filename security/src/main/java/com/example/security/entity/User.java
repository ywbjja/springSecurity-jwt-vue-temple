package com.example.security.entity;

import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/4
 * @Description：
 */
@Data
public class User {

    private String uid;

    private String avatar;

    private String username;

    private String nickname;

    private String password;

    private String phone;

    private String mail;

    private Integer state;

    private Date addTime;

    private Date upTime;

    private Integer dept;

    private Set<Role> roles;
}
