package com.huihui.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.huihui.pojo.Role;
import com.huihui.pojo.User;
import com.huihui.service.role.RoleServiceImpl;
import com.huihui.service.user.UserServiceImpl;
import com.huihui.utils.Constants;
import com.huihui.utils.PageSupport;
import com.mysql.jdbc.StringUtils;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 辉辉
 * @Date: 2021-08-25 16:10
 * @Description:
 * @version: 1.0
 */
public class UserServlet extends HttpServlet {
    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //######为了实现复用
        String method = req.getParameter("method");//用于判断掉用的是哪个方法
        if(method.equals("savepwd")&&method!=null) {//获取method的值等于savepwd就是调用修改密码方法
            this.savepwd(req,resp );                //如果是请求了修改密码的请求就调用修改密码的方法
        }else if(method.equals("pwdmodify")&&method!=null) {//通过判断参数method的值发现是pwdmodify调用的方法是查询旧密码方法
            this.pwdmodify(req,resp);
        }else if(method.equals("query")&&method!=null){//如果是query参数请求就返回查询列表方法
            this.query(req, resp);
        }else if(method.equals("view")&&method!=null){//如果是view参数请求就返回查个人详情方法
                this.view(req, resp);
        }else if(method.equals("modify")&&method!=null){//如果是modify参数请求就返回修改方法
            this.modify(req,resp);
        }else if(method.equals("getrolelist")&&method!=null) {//如果是getrolelist参数请求就返回角色列表
            this.getrolelist(req,resp);
        }else if (method.equals("modifyexe")&&method!=null) {//如果是modifyexe参数请求就调用修改方法进行修改传参
            this.modifyexe(req,resp);
        }else if(method.equals("deluser")&&method!=null) {//如果是deluser参数请求就调用修改方法进行用户删除
            this.deluser(req,resp);
        }else if(method.equals("ucexist")&&method!=null) {//如果是ucexist参数请求就调用判断用户是否存在方法
            this.ucexist(req,resp);
        }else if(method.equals("add")&&method!=null){//如果是add参数请求就调用添加用户方法
            this.add(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //修改密码方法
    public void savepwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        Object user = req.getSession().getAttribute(Constants.USER_SESSION);//获得session中的id

        String rnewpassword = req.getParameter("rnewpassword");//新密码

        if(user!=null && !StringUtils.isNullOrEmpty(rnewpassword)) {//判断user用户不等于null 新密码不等于null and 新密码长度不为0
            UserServiceImpl userService = new UserServiceImpl();
            Boolean aBoolean = userService.updatePwd(((User) user).getId(), rnewpassword);
            if(aBoolean){
                req.setAttribute("message", "密码修改成功，请退出使用新密码进行登录！");
                //密码修改成功移除用户session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                req.setAttribute("message", "密码修改失败！");
            }
        }else {
            req.setAttribute("message", "新密码有问题！");
        }
        //页面转发
        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
    }

    //通过session中的用户数据判断旧密码是否正确
    public void pwdmodify(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Object user = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");//获取网页传输的旧密码

        //创建一个结果集用户返回给前端
        Map<String, String> resultMap = new HashMap<>();

        if(user==null){//当前用户session过期，请重新登录
            resultMap.put("result", "sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){//旧密码输入为空
            resultMap.put("result", "error");
        }else {
            //进行判断密码不为空且密码长度大于0 且对象user不为空进行 旧密码判断
            if(((User)user).getUserPassword().equals(oldpassword)){//密码一致
                resultMap.put("result", "true");
            }else{//密码不一致
                resultMap.put("result", "false");
            }
        }
            //应为我们的请求是ajax需要返回一个json
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //这里可以使用阿里巴巴的转换JSON类不过需要先导入jar包
            //把map转换成json
            //JSONArray 阿里巴巴的JSON工具类, 转换格式
            /*
            resultMap = ["result","sessionerror","result","error"]
            Json格式 = {key：value}
             */
            writer.print(JSONArray.toJSONString(resultMap));//把json文件传入到前端
            writer.flush();
            writer.close();
    }

    //获取用户列表方法
    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //查询用户列表
        //从前端获取数据
        String queryname = req.getParameter("queryname");//首先获取要查询的名字
        String queryUserRole = req.getParameter("queryUserRole");//获取角色名称
        String pageIndex = req.getParameter("pageIndex");//当前页
       int queryRole=0;//角色下拉框如果没有选择默认设置为0 如果是0后台不会进行处理

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();

        //第一次走这个请求，一定是第一页，页面大小固定的；
        //int pageSize = 5; //可以把这个些到配置文件中，方便后期修改；设置每一页的数据条数 Constants.PAGE_SIZE
        int currentPageNo = 1;//如果页码为空使用默认值1第一页

       if(queryname==null){//判断如果用户没有输入名字就设置为空
           queryname="";
       }
       if(queryUserRole!=null&&!queryUserRole.equals("")){//判断角色不为空
           queryRole=Integer.parseInt(queryUserRole);//如果角色下拉框被选中了 //给查询赋值！0,不处理1,系统管理员2,经理3普通员工
       }
       if(pageIndex!=null){//判断页码是否为空
           currentPageNo = Integer.parseInt(pageIndex);
       }

       //获取用户总数(分页会存储上一页跟下一页的情况)
        int userCount = userService.getUserCount(queryname, queryRole);
       //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);//设置页码总数
        pageSupport.setPageSize(Constants.PAGE_SIZE);//设置每页最大显示条数
        pageSupport.setTotalPageCount(userCount);//设置总页数

        //控制首页和尾页
        int totalPageCount = ((int)(userCount/Constants.PAGE_SIZE))+1;

        //如果页面要小于1了，就显示第一页的东西
        if (currentPageNo<1){
            currentPageNo = 1;
        }else if (currentPageNo>totalPageCount){ //当前页面大于了最后一页；
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        List<User> userList = userService.getUserList(queryname, queryRole, currentPageNo, Constants.PAGE_SIZE);
        req.setAttribute("userList", userList);
        //获取角色列表
        RoleServiceImpl roleService = new RoleServiceImpl();

        req.setAttribute("roleList", roleService.getRoleList());

        req.setAttribute("totalCount", userCount);//数据总数
        req.setAttribute("currentPageNo", currentPageNo);//当前页码
        req.setAttribute("totalPageCount", totalPageCount);//总页数
        req.setAttribute("queryUserName",queryname);
        req.setAttribute("queryUserRole",queryRole);
        //返回前端
        req.getRequestDispatcher("userlist.jsp").forward(req,resp );

    }

    //根据id查询用户
    public void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, ParseException {
        String uid = req.getParameter("uid");//获取要显示的id
        if(uid!=null&&!uid.equals("")){//判断uid不为空
            UserServiceImpl userService = new UserServiceImpl();
            //调用查询用户方法
            User userId = userService.getUserId(Integer.parseInt(uid));

            Date birthday = userId.getBirthday();//获取出生日期发现时间为Tue Apr 16  00:00:00 CST 2019
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //格式化规则把Tue Apr 16 00:00:00 CST 2019转为yyyy-MM-dd
            String strDate= sdf.format(birthday); //格式化成yyyy-MM-dd格式的时间字符串
            Date newDate =sdf.parse(strDate);
            java.sql.Date resultDate = new java.sql.Date(newDate.getTime());
            //把格式转换后的日期重新传入
            userId.setBirthday(resultDate);
            //调用方法进行查询
            req.setAttribute("user", userId);//把查询到的用户信息传入前端
            req.getRequestDispatcher("userview.jsp").forward(req,resp );
            //重定向到显示用户详情页面
        }else {
            //如果uid为空这返回错误
            resp.sendRedirect("/error.jsp");
        }

    }

    //修改页面展示
    public void modify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, ParseException {
        String uid = req.getParameter("uid");
        if(!StringUtils.isNullOrEmpty(uid)){//判断uid不为空
            UserServiceImpl userService = new UserServiceImpl();
            //调用方法进行根据id查询用户
            User userId = userService.getUserId(Integer.parseInt(uid));
            //出生日期格式转换
            Date birthday = userId.getBirthday();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //格式化规则把Tue Apr 16 00:00:00 CST 2019转为yyyy-MM-dd
            String strDate= sdf.format(birthday); //格式化成yyyy-MM-dd格式的时间字符串
            Date newDate = sdf.parse(strDate);

            java.sql.Date resultDate = new java.sql.Date(newDate.getTime());
            //重新传入日期
            userId.setBirthday(resultDate);
            //调用方法进行查询
            req.setAttribute("user", userId);//把查询到的用户信息传入前端
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp );
        }else {
            //如果uid为空这返回错误
            resp.sendRedirect("/error.jsp");
        }
    }

    //返回角色列表
    public void getrolelist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoleServiceImpl roleService = new RoleServiceImpl();
        //调用查询角色列表方法返回角色集合
        List<Role> roleList = roleService.getRoleList();
        if(roleList!=null){//判断查询是否成功
            //返回给前端JSON角色列表数据

            //应为我们的请求是ajax需要返回一个json
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //这里可以使用阿里巴巴的转换JSON类不过需要先导入jar包
            //把map转换成json
            //JSONArray 阿里巴巴的JSON工具类, 转换格式
            /*
            resultMap = ["result","sessionerror","result","error"]
            Json格式 = {key：value}
             */
            writer.print(JSONArray.toJSONString(roleList));
            writer.flush();//刷新
            writer.close();//关闭流
        }
    }

    //进行用户数据修改
   public void modifyexe(HttpServletRequest req, HttpServletResponse resp) throws ParseException, IOException, ServletException {
       String uid = req.getParameter("uid");//获取id
       String userName = req.getParameter("userName");//获取用户名
       String gender = req.getParameter("gender");//获取性别
       String birthday = req.getParameter("birthday");//获取出生日期
       String phone = req.getParameter("phone");//获取电话
       String address = req.getParameter("address");//获取地址
       String userRole = req.getParameter("userRole");//获取角色

       if(!StringUtils.isNullOrEmpty(uid)){//判断id不为空
           UserServiceImpl userService = new UserServiceImpl();

           User user = new User();//把前端的数据封装到user类中
           user.setId(Integer.parseInt(uid));//设置id
           user.setUserName(userName);//设置用户名
           user.setGender(Integer.parseInt(gender));//设置性别
           user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));//设置出生日期
           user.setPhone(phone);//设置电话
           user.setAddress(address);//设置地址
           user.setUserRole(Integer.parseInt(userRole));//设置角色
           user.setModifyBy(((User)req.getSession().getAttribute(Constants
                   .USER_SESSION)).getId());//设置跟新数据的更新者id
           user.setModifyDate(new Date());//设置更新数据时间
           //调用修改方法传入对应的参数值
           int i = userService.setUpdateUser(user);
           //字符串需要转成Date类型
            if (i>0){//如果返回值大于一代表修改成功了
                //重定向到显示用户列表页面
                resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");

            }else {//否则修改失败 转发到修改页面
                req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
            }

       }else {
           //如果uid为空这返回错误
           resp.sendRedirect("/error.jsp");
       }
   }

   //删除用户
    public void deluser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uid = req.getParameter("uid");

        HashMap<String, String> resultMap= new HashMap<>();
        if(!StringUtils.isNullOrEmpty(uid)){//判断uid是否为空
            UserServiceImpl userService = new UserServiceImpl();
            //调用删除方法
            int i = userService.setDeleteUser(Integer.parseInt(uid));
            if(i>0){//代表删除成功
                //这里设置的数据因为js中ajax需要用到的返回值

                /*
                if(data.delResult == "true"){//删除成功：移除删除行
						alert("删除成功");
						obj.parents("tr").remove();
					}else if(data.delResult == "false"){//删除失败
						alert("对不起，删除用户【"+obj.attr("username")+"】失败");
					}else if(data.delResult == "notexist"){
						alert("对不起，用户【"+obj.attr("username")+"】不存在");
					}
                 */
                resultMap.put("delResult", "true");
            }else {
                resultMap.put("delResult", "false");
            }

         }else {
            resultMap.put("delResult", "notexist");
        }

        //设置前端传输的数据格式
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();//创建写出流
        //这里可以使用阿里巴巴的转换JSON类不过需要先导入jar包
        //把map转换成json
        //JSONArray 阿里巴巴的JSON工具类, 转换格式

        writer.print(JSONArray.toJSONString(resultMap));//把json数据传输到前端的ajax中
        writer.flush();
        writer.close();

    }

    //根据UserCode判断用户是否存在
    public void ucexist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userCode = req.getParameter("userCode");
        if(!StringUtils.isNullOrEmpty(userCode)){//判断userCode不为空
            UserServiceImpl userService = new UserServiceImpl();
            //调用方法进行查询userCode是否存在
            Boolean Code1boole = userService.getUserCode(userCode);

            HashMap<String, String> reulsMap = new HashMap<>();
            //如果存在返回exist
            if(Code1boole){
                reulsMap.put("userCode", "exist");
            }else {//否则随意返回一个字符串
                reulsMap.put("userCode", "adopt");
            }
            //设置数据格式为json
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.print(JSONArray.toJSONString(reulsMap));//把数据传输到前端
            writer.flush();
            writer.close();
        }
    }

    //添加用户
    public void add(HttpServletRequest req, HttpServletResponse resp) throws ParseException, IOException, ServletException {
        String userCode = req.getParameter("userCode");//获取用户编码
        String userName = req.getParameter("userName");//获取用户名
        String userPassword = req.getParameter("userPassword");//获取密码
        String gender = req.getParameter("gender");//获取性别
        String birthday = req.getParameter("birthday");//获取出生日期
        String phone = req.getParameter("phone");//获取电话
        String address = req.getParameter("address");//获取地址
        String userRole = req.getParameter("userRole");//获取角色

        User user = new User();//把数据封装到user对象中

        user.setUserCode(userCode);//设置用户编码
        user.setUserName(userName);//设置用户名
        user.setUserPassword(userPassword);//设置密码
        user.setGender(Integer.parseInt(gender));//设置性别
        user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));//设置出生日期（把字符串转为Date类型）
        user.setPhone(phone);//设置电话
        user.setAddress(address);//设置地址
        user.setUserRole(Integer.parseInt(userRole));//设置用户角色
        user.setCreationDate(new Date());//设置创建日期
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants
                .USER_SESSION)).getId());//设置创建者id

        //调用添加用户方法
        UserServiceImpl userService = new UserServiceImpl();
        int i = userService.addUser(user);
        if(i>0){
            //数据添加成功页面重定向到显示所有用户页面
            resp.sendRedirect(req.getContextPath() + "/jsp/user" +
                    ".do?method=query");

        }else {
            //数据添加失败页面转发到添加用户页面
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
    }
}
