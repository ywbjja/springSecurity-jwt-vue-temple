package com.example.security.mapper;

import com.example.security.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/4
 * @Description：
 */
@Mapper
public interface UserMapper {

    User selectByUserName(String username);
}
