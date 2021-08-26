package com.huihui.servlet.user;

import com.huihui.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: 辉辉
 * @Date: 2021-08-25 12:20
 * @Description:
 * @version: 1.0
 */
public class LogoutServlet extends HttpServlet {
    @Override//注销登录
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().removeAttribute(Constants.USER_SESSION);//删除session
        resp.sendRedirect(req.getContextPath()+"/login.jsp");//返回到登录页面
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
