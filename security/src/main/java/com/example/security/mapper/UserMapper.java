package com.example.security.mapper;

import com.example.security.entity.User;
import com.example.security.util.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/4
 * @Description：
 */

public interface UserMapper {

    User selectByUserName(String username);
}
