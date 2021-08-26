package com.huihui.servlet.user;

import com.huihui.pojo.User;
import com.huihui.service.user.UserServiceImpl;
import com.huihui.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author: 辉辉
 * @Date: 2021-08-25 12:18
 * @Description:
 * @version: 1.0
 */
public class LoginServlet extends HttpServlet {

    //servlrt控制层调用业务层
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("login ============ " );

        String userCode = req.getParameter("userCode");//获取输入的账号
        String userPassword = req.getParameter("userPassword");//获取输入的密码
        UserServiceImpl userService = new UserServiceImpl();//调用方法进行判断
        User user = userService.login(userCode, userPassword);//从数据库进行查询

        if(user!=null){//判断登录是否成功
            HttpSession session = req.getSession();
            //放入session
            session.setAttribute(Constants.USER_SESSION, user);
            //页面跳转
            resp.sendRedirect("jsp/frame.jsp");

        }else {
            //页面跳转（login.jsp）带出提示信息--转发
            req.setAttribute("error", "账号或密码错误");
            req.getRequestDispatcher("login.jsp").forward(req,resp );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
