package com.example.security.util;

import com.example.security.entity.Role;
import com.example.security.entity.User;
import lombok.Data;

import java.util.Set;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/8
 * @Description：这个是在User类的基础上添加需要给前端返回用户的角色权限资源列表
 */
@Data
public class UserVo  {

    private String id;


    private String username;



    //角色列表（用户和角色是多对多的关系)

    private Set<Role> roleSet;

    //菜单列表
    private Set<Menu> menuSet;

    public UserVo(String id, String username,  Set<Role> roleSet, Set<Menu> menuSet) {
        this.id = id;
        this.username = username;
        this.roleSet = roleSet;
        this.menuSet = menuSet;
    }
}
