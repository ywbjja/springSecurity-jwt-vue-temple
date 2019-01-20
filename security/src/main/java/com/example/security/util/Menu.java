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
    private String name;
    private String path;
    private String component;
    private String redirect;
    private MenuMetaVo meta;
    private List<Menu> children;
    private Boolean alwaysShow;


}
