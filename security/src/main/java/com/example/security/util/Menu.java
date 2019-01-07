package com.example.security.util;

import lombok.Data;

import java.util.List;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：菜单类，这个是为了生成权限菜单树形结构
 */
@Data
public class Menu {
    private Integer per_id;
    private Integer per_paerent_id;
    private String per_name;
    private String per_resource;
    private List<Menu> children;
    public Menu(){

    }
    public Menu(Integer per_id,Integer per_paerent_id,String per_name,String per_resource){
        this.per_id = per_id;
        this.per_paerent_id = per_paerent_id;
        this.per_name = per_name;
        this.per_resource = per_resource;
    }
}
