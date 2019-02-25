package com.example.security.service.impl;

import com.example.security.entity.Permission;
import com.example.security.entity.RolePermission;
import com.example.security.mapper.PermissionMapper;
import com.example.security.service.PermissionService;
import com.example.security.util.GenTree;
import com.example.security.util.RetCode;
import com.example.security.util.RetResult;
import com.example.security.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/22
 * @Description：
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;




    /**
     * 更新菜单
     * @param map
     * @return
     */
    @Override
    public RetResult update(Map<String,Object> map) {
        if(map.get("per_id") == null){
            return new RetResult(RetCode.FAIL.getCode(),"id不能为空");
        }
        return  new RetResult(RetCode.SUCCESS.getCode(),permissionMapper.update(map));
    }

    /**
     * 添加菜单
     * @param map
     * @return
     */
    @Override
    public RetResult add(Map<String,Object> map) {
        if( map.get("per_resource") ==null){
            return new RetResult(RetCode.FAIL.getCode(),"Id,Url路径不能为空");
        }
        Permission permission = new Permission(map);
        SnowFlake snowFlake = new SnowFlake(2,3);
        permission.setPer_id(snowFlake.nextId());
        log.info(permission.getPer_id().toString());
        permission.setPer_crtTime(Timestamp.valueOf(LocalDateTime.now()));
        permission.setPer_type("menu");
        return new RetResult(RetCode.SUCCESS.getCode(),"添加成功",permissionMapper.add(permission));
    }


    /**
     * 获取所有菜单树，树形表格数据
     * @param map
     * @return
     */
    @Override
    public RetResult queryAllMenusTree(Map<String, Object> map) {
        return new RetResult(RetCode.SUCCESS.getCode(), GenTree.genRoot(permissionMapper.getAllMenuTree()));
    }

    @Override
    public RetResult getPerIdList(Map<String, Object> map) {
        if(map.get("rp_role_id") == null){
            return new RetResult(RetCode.FAIL.getCode(),"id不能为空");
        }

        return new RetResult(RetCode.SUCCESS.getCode(),permissionMapper.getPerIdList(Long.parseLong(map.get("rp_role_id").toString())));
    }

    @Override
    public RetResult addRP(Map<String, Object> map) {
        if (map.get("rp_role_id") == null) {
            return new RetResult(RetCode.FAIL.getCode(), "角色id不能为空");
        } else {
            if (permissionMapper.getCount(Long.parseLong(map.get("rp_role_id").toString())) > 0){
                permissionMapper.del(Long.parseLong(map.get("rp_role_id").toString()));
            }
            List list = (List) map.get("checkIds");;
            if(list != null && list.size() > 0){
                List<Long> longList = new ArrayList<>();
                for (Object id : list){
                    longList.add(Long.valueOf(id.toString()));
                }
                for (Long ids : longList) {
                    RolePermission rolePermission = new RolePermission(map);
                    SnowFlake snowFlake = new SnowFlake(2,3);
                    rolePermission.setRp_id(snowFlake.nextId());
                    rolePermission.setRp_per_id(ids);
                    permissionMapper.addRP(rolePermission);
                }
            }
        }

        return new RetResult(RetCode.SUCCESS.getCode(), "更新成功");
    }

    @Override
    public RetResult del(Map<String,Object> map) {
        if(map.get("per_id") == null){
            return new RetResult(RetCode.FAIL.getCode(),"菜单id不能为空");
        }
        return new RetResult(RetCode.SUCCESS.getCode(),permissionMapper.delByPerid(Long.parseLong(map.get("per_id").toString())));
    }

}
