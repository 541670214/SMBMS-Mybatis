package com.huihui.filter;

import javax.servlet.*;
import java.io.IOException;
/**
 * @Author: 辉辉
 * @Date: 2021-08-26 19:33
 * @Description:
 * @version: 1.0
 */
public class CharacterEncodeingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //解决乱码 过滤器
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
