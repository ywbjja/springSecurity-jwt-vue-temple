package com.example.security.entity;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/24
 * @Description：
 */
@Data
public class RolePermission {

    private Long rp_id;
    private Long rp_per_id;
    private Long rp_role_id;

    public RolePermission(Map<String,Object> map) {
        if(map.get("rp_id") != null){
            this.rp_id = Long.valueOf(map.get("rp_id").toString());
        } else {
            this.rp_id = null;
        }
        if(map.get("rp_per_id") != null){
            this.rp_per_id = Long.valueOf(map.get("rp_per_id").toString());
        } else {
            this.rp_per_id = null;
        }
        if(map.get("rp_role_id") != null){
            this.rp_role_id = Long.valueOf(map.get("rp_role_id").toString());
        } else {
            this.rp_role_id = null;
        }

    }
}
