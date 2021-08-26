package com.huihui.filter;

import com.huihui.pojo.User;
import com.huihui.utils.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 13:54
 * @Description:
 * @version: 1.0
 */

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override//权限验证，判断是否登录
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        //获取session
        User user =(User)req.getSession().getAttribute(Constants.USER_SESSION);
        if(user==null){//判断session是否存在，不存在就需要登录
            resp.sendRedirect("/error.jsp");
        }else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
