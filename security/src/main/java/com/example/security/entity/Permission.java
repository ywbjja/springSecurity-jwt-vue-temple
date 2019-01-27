package com.example.security.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：
 */
@Data
public class Permission {


    @JsonSerialize(using = ToStringSerializer.class)
    private Long per_id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long per_parent_id;
    private String per_name;
    private String per_resource;
    private String per_type;
    private String per_icon;
    private String per_describe;
    private String per_component;
    private Integer per_sort;
    private Timestamp per_crtTime;
    private List<Permission> children;

    public Permission() {
    }

    public Permission(Long per_id, Long per_parent_id, String per_name, String per_resource, String per_type, String per_icon, String per_describe, String per_component,
                      Integer per_sort, Timestamp per_crtTime ,List<Permission> children) {
        this.per_id = per_id;
        this.per_parent_id = per_parent_id;
        this.per_name = per_name;
        this.per_resource = per_resource;
        this.per_type = per_type;
        this.per_icon = per_icon;
        this.per_describe = per_describe;
        this.per_component = per_component;
        this.per_sort = per_sort;
        this.per_crtTime = per_crtTime;
        this.children = children;
    }
    public Permission(Map<String,Object> map){
        if(map.get("per_id") != null){
            this.per_id = Long.valueOf(map.get("per_id").toString());
        } else {
            this.per_id = null;
        }
        this.per_name = (String)map.get("per_name");
        if (map.get("per_parent_id") != null) {
            this.per_parent_id = Long.valueOf(map.get("per_parent_id").toString());
        } else {
            this.per_parent_id = null;
        }
        this.per_resource = (String)map.get("per_resource");
        this.per_type = (String)map.get("per_per_type");
        this.per_icon = (String)map.get("per_icon");
        this.per_describe = (String)map.get("per_describe");
        this.per_component = (String)map.get("per_component");
        this.per_sort = (Integer)map.get("per_sort");
        if(map.get("per_crtTime") != null){
            this.per_crtTime = Timestamp.valueOf(map.get("per_crtTime").toString());
        } else {
            this.per_crtTime = null;
        }

    }
}
