package com.example.security.service.impl;

import com.example.security.entity.Permission;
import com.example.security.mapper.PermissionMapper;
import com.example.security.service.PermissionService;
import com.example.security.util.RetCode;
import com.example.security.util.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
