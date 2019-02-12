package com.example.security.util;

import com.example.security.entity.Permission;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * @Autoor:杨文彬
 * @Date:2019/1/18
 * @Description：设置给前台用的路由菜单
 */
@Slf4j
public class BulidTree {

    public static List<Menu> genRoot(List<Permission> permissions){

        List<Menu> trees = new LinkedList<Menu>();
        permissions.forEach(permission -> {
            if(permission != null){
                List<Permission> permissionList = permission.getChildren();
                Menu menu = new Menu();
                menu.setName(permission.getPer_name());
                menu.setPath(permission.getPer_resource());
                if(permission.getPer_parent_id().toString().equals("0")){
                    //一级菜单的path需要加'/'
                    menu.setPath("/"+permission.getPer_resource());
                    //判断component是否为空如果是空返回Layout给前端
                    menu.setComponent(StringUtils.isEmpty(permission.getPer_component()) ?"Layout" : permission.getPer_component());
                }else{
                    menu.setComponent(permission.getPer_component());
                }
                menu.setMeta(new MenuMetaVo(permission.getPer_name(),permission.getPer_icon()));
                //判断是否有子路由
                if(permissionList != null && permissionList.size() != 0){
                    menu.setAlwaysShow(true);
                    menu.setRedirect("noredirect");
                    menu.setChildren(genRoot(permissionList));
                } else if (permission.getPer_parent_id().toString().equals("0")){
                    Menu menu1 = new Menu();
                    menu1.setMeta(menu.getMeta());
                    menu1.setPath("index");
                    menu1.setName(menu.getName());
                    menu1.setComponent(menu.getComponent());
                    menu.setName(null);
                    menu.setMeta(null);
                    menu.setComponent("Layout");
                    List<Menu> menuList = new ArrayList<Menu>();
                    menuList.add(menu1);
                    menu.setChildren(menuList);
                }
                trees.add(menu);

            }

        });
        return trees;
    }

}
