package com.example.security.service.impl;

import com.example.security.entity.Role;
import com.example.security.mapper.RoleMapper;
import com.example.security.service.RoleService;
import com.example.security.util.PageUtil;
import com.example.security.util.RetCode;
import com.example.security.util.RetResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/7
 * @Description：
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService
{

    @Autowired
    private RoleMapper roleMapper;


    @Override
    public RetResult getRoleListByCond(Map<String, Object> map) {

        Integer pageCur  = Integer.valueOf(map.get("curPageNum").toString());
        Integer pageSize = Integer.valueOf(map.get("pageSize").toString());

        /**
         * 分页数据
         */
        Page page = PageHelper.startPage(pageCur,pageSize);
        ArrayList list = roleMapper.getRoleListByCond(map);
        PageUtil pageData = new PageUtil(page,list);
        return new RetResult(RetCode.SUCCESS.getCode(),"查询成功",pageData);
    }

    @Override
    public RetResult getAllRoleList(Map<String, Object> map) {
        List<Role> roles = roleMapper.getAllRoleList();
        List<Map<String,Object>> roleList = new ArrayList<>();
        for (Role role :roles ){
            Map<String,Object> map1 = new HashMap<>();
            map1.put("id",role.getId());
            map1.put("label",role.getRolename());
            roleList.add(map1);
        }
        return new RetResult(RetCode.SUCCESS.getCode(),roleList);
    }

    @Override
    public RetResult getRoleListByPerId(Map<String, Object> map) {
        List<Long> ids = new ArrayList<>();
        List<Role> roleList = roleMapper.getRoleListByPerId(Long.parseLong(map.get("per_id").toString()));
        for (Role role : roleList) {
            Long id = role.getId();
            ids.add(id);
        }
        return new RetResult(RetCode.SUCCESS.getCode(),ids);
    }
}
