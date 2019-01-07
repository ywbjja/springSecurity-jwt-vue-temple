package com.example.security.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：把数据库中的list转成树形结构
 */
public class GenTree {


    /**
     * 递归根节点
     * @param nodes
     * @return
     */
    public static Set<Menu> genRoot(Set<Menu> nodes){
        //这个是根节点
        Set<Menu> root = new HashSet<>();
        //遍历数据
        nodes.forEach(menu -> {
            //当父id是0的时候应该是根节点
            if(menu.getPer_paerent_id() == 0){
                root.add(menu);
            }
        });
        //这里是遍历根节点然后看有没有子节点
        root.forEach(menu -> {
            genChildren(menu,nodes);
        });
        //返回数据
        return root;
    }

    /**
     * 递归子节点
     * @param menu
     * @param nodes
     * @return
     */
    private static Menu genChildren(Menu menu, Set<Menu> nodes) {
        //遍历传过来的数据
        for (Menu menu1 :nodes){
            //如果数据中的父id和上面的per_id一致应该就放children中去
            if(menu.getPer_id().equals(menu1.getPer_paerent_id())){
                //如果当前节点的子节点是空的则初始化，如果不为空就加进去
                if(menu.getChildren() == null){
                    menu.setChildren(new ArrayList<Menu>());
                }
                menu.getChildren().add(genChildren(menu,nodes));
            }
        }
        //返回数据
        return menu;
    }
}
