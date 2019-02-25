package com.example.security.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/4
 * @Description：
 */
@Data
public class Role {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    private String rolename;

    private String roledesc;

    private Timestamp createTime;


    //权限的列表
    private ArrayList<Permission> permissions;

    public Role() {
    }

    public Role(Long id, String rolename, String roledesc, Timestamp createTime, ArrayList<Permission> permissions) {
        this.id = id;
        this.rolename = rolename;
        this.roledesc = roledesc;
        this.createTime = createTime;
        this.permissions = permissions;
    }
}
