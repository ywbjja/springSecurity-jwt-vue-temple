package com.example.security.util;

import com.example.security.entity.Permission;

import java.util.*;

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
    public static List<Permission> genRoot(List<Permission> nodes){
        List<Permission> root = new ArrayList<Permission>();
        //遍历数据
        nodes.forEach(permission -> {
            //当父id是0的时候应该是根节点
            if(permission.getPer_parent_id() == 0){
                root.add(permission);
            }
        });
        //这里是子节点的创建方法
        root.forEach(permission -> {
            genChildren(permission,nodes);
        });
        //返回数据
        return root;
    }

    /**
     * 递归子节点
     * @param permission
     * @param nodes
     * @return
     */
    private static Permission genChildren(Permission permission, List<Permission> nodes) {
        //遍历传过来的数据
        for (Permission permission1 :nodes){
            //如果数据中的父id和上面的per_id一致应该就放children中去
            if(permission.getPer_id().equals(permission1.getPer_parent_id())){
                //如果当前节点的子节点是空的则初始化，如果不为空就加进去
                if(permission.getChildren() == null){
                    permission.setChildren(new ArrayList<Permission>());
                }
                permission.getChildren().add(genChildren(permission1,nodes));
            }
        }
        //返回数据
        return permission;
    }

   
}
