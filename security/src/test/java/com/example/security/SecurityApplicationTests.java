package com.example.security;

import com.example.security.mapper.PermissionMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityApplicationTests {

    @Autowired
    private PermissionMapper permissionMapper;
    @Test
    public void contextLoads() {
        System.out.println(permissionMapper.getAllMenuTree());
    }

}

