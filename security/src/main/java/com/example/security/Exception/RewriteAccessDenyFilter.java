package com.example.security.Exception;

import com.alibaba.fastjson.JSON;
import com.example.security.util.RetCode;
import com.example.security.util.RetResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/28
 * @Description：
 */
@Component
public class RewriteAccessDenyFilter implements AccessDeniedHandler  {


    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        RetResult retResult = new RetResult(RetCode.NODEFINED.getCode(),"抱歉，您没有访问该接口的权限");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(retResult));
    }
}
