package com.huihui.servlet.provider;

import com.alibaba.fastjson.JSONArray;
import com.huihui.pojo.Provider;
import com.huihui.pojo.User;
import com.huihui.service.provider.ProviderServiceImpl;
import com.huihui.utils.Constants;
import com.mysql.jdbc.StringUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-25 20:46
 * @Description:
 * @version: 1.0
 */
public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //这样可以实现复用
        String method = req.getParameter("method");//通过请求的参数用于判断掉用的是哪个方法

        if (method!=null&&method.equals("query")){//获取method的值等于query就是调用获取供应商方法
            this.getQuery(req,resp);
        }else if (method!=null&&method.equals("view")) {//获取method的值等于view就是调用获取根据id查询供应商方法
            this.getView(req,resp,"providerview.jsp");
        }else if(method!=null&&method.equals("modify")) {//获取method的值等于modify就是调用获取根据id查询供应商方法（但是返回的是修改页面，把查询的值做默认值）
            this.getModify(req,resp);
        }else if(method!=null&&method.equals("modifysave")) {//获取method的值等于modifysave就是调用根据id修改供应商方法
            this.setModifysave(req,resp);
        }else if(method!=null&&method.equals("delprovider")) {//获取method的值等于delprovider就是调用根据id删除供应商方法
            this.Delprovider(req,resp);
        }else if(method!=null&&method.equals("add")) {//获取method的值等于add就是调用添加供应商方法
            this.add(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //查询供应商列表
    private void getQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryProCode = req.getParameter("queryProCode");//获取前端输入的供应商编码
        String queryProName = req.getParameter("queryProName");//获取前端输入的供应商名称
        if(StringUtils.isNullOrEmpty(queryProCode)){//判断供应商编码是否为空
            // 如果为null如果为null就设置为空字符串方便sql查询%""%所以数据)
            queryProCode="";
        }
        if (StringUtils.isNullOrEmpty(queryProName)){//判断供应商名称是否为空
            queryProName="";
        }
        ProviderServiceImpl provider = new ProviderServiceImpl();
        List<Provider> providerList = provider.getProvider(queryProCode,
                queryProName);//调用查询供应商方法
        req.setAttribute("providerList", providerList);//把查询结果传入到前端（用于前端解析）
        req.setAttribute("queryProCode", queryProCode);//把查询输入的供应商编码传入到指定的查询框（友好提升）
        req.setAttribute("queryProName", queryProName);
        //页面转发
        req.getRequestDispatcher("providerlist.jsp").forward(req,resp );

    }

    //根据id查询供应商显示详情
    private void getView(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String id = req.getParameter("proid");//获取要查询的id
        if(!StringUtils.isNullOrEmpty(id)){//判断id不为空
            ProviderServiceImpl provider = new ProviderServiceImpl();
            Provider providerId = provider.getProviderId(Integer.parseInt(id));//调用根据id查询供应商方法
            req.setAttribute("provider", providerId);//把查询结果传入前端
            req.getRequestDispatcher(url).forward(req,resp );//页面转发到查看页面
        }else {//id为空返回error页面
            resp.sendRedirect( "/error.jsp");
        }
    }

    //根据id查询供应商返回修改信息页面
    private void getModify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.getView(req, resp,"providermodify.jsp");//调用根据id查询供应商然后返回修改页面
        //这里因为我们的代码跟显示详情页面是一样的就是最后返回页面不一样，所以就直接调用详细页面然后传入修改页面的url就可以了
    }

    //根据id修改供应商信息
    private void setModifysave(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");//获取供应商id
        String proCode = req.getParameter("proCode");//获取供应商编码
        String proName = req.getParameter("proName");//获取供应商名称
        String proContact = req.getParameter("proContact");//获取供应商联系人
        String proPhone = req.getParameter("proPhone");//获取供应商电话
        String proAddress = req.getParameter("proAddress");//获取供应商地址
        String proFax = req.getParameter("proFax");//获取供应商传真
        String proDesc = req.getParameter("proDesc");//获取供应商描述

        Provider provider = new Provider();
        provider.setId(Integer.parseInt(id));
        provider.setProCode(proCode);//设置供应商编码
        provider.setProName(proName);//设置供应商名称
        provider.setProContact(proContact);//设置供应商联系人
        provider.setProPhone(proPhone);//设置供应商电话
        provider.setProAddress(proAddress);//设置供应商地址
        provider.setProFax(proFax);//设置供应商传真
        provider.setProDesc(proDesc);//设置供应商描述
        provider.setModifyBy(((User)req.getSession().getAttribute(Constants
                .USER_SESSION)).getId());//设置更新者id（通过登录的用户的session中获取id）
        provider.setModifyDate(new Date());//设置更新数据

        ProviderServiceImpl providermethod = new ProviderServiceImpl();
        int updateProvider = providermethod.getUpdateProvider(provider);//调用修改供应商方法传入供应商对象
        if (updateProvider>0){//判断是否修改成功
            //重定向到查询供应商页面
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        }else {
            req.getRequestDispatcher("providermodify.jsp").forward(req,resp );
        }

    }

    //根据id删除供应商
    private void Delprovider(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("proid");//获取id
        HashMap<String, String> resultMap = new HashMap<>();
        if(!StringUtils.isNullOrEmpty(id)){//判断id不等于空
            ProviderServiceImpl provider = new ProviderServiceImpl();
            int delprovider = provider.Delprovider(Integer.parseInt(id));//调用删除方法

            if (delprovider == 0){//判断是否删除成功
                resultMap.put("delResult", "true");
            }else if(delprovider == -1) {//不成功返回false
                resultMap.put("delResult", "false");
            }else {//供应商还有订单为处理完成订单数为
                resultMap.put("delResult", String.valueOf(delprovider));
            }
        }else {//id为空返回notexist
            resultMap.put("delResult", "notexist");
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //添加供应商
    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String proCode = req.getParameter("proCode");//获取供应商编码
        String proName = req.getParameter("proName");//获取供应商名称
        String proContact = req.getParameter("proContact");//获取供应商联系人
        String proPhone = req.getParameter("proPhone");//获取供应商电话
        String proAddress = req.getParameter("proAddress");//获取供应商地址
        String proFax = req.getParameter("proFax");//获取供应商传真
        String proDesc = req.getParameter("proDesc");//获取供应商描述

        Provider provider = new Provider();//创建供应商实体类
        provider.setProCode(proCode);//设置供应商编码
        provider.setProName(proName);//设置供应商名称
        provider.setProContact(proContact);//设置供应商联系人
        provider.setProPhone(proPhone);//设置供应商电话
        provider.setProAddress(proAddress);//设置供应商地址
        provider.setProFax(proFax);//设置供应商传真
        provider.setProDesc(proDesc);//设置供应商描述
        provider.setCreatedBy(((User)req.getSession().getAttribute(Constants
                .USER_SESSION)).getId());//设置供应商创建者id通过session中获取登录用户的id
        provider.setCreationDate(new Date());//设置供应商创建时间

        ProviderServiceImpl providerService = new ProviderServiceImpl();
        int i = providerService.addProvider(provider);//调用添加供应商方法
        if(i>0){//添加成功页面重定向到查询供应商列表页面
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        }else {//页面转发打添加页面
            req.getRequestDispatcher("provideradd.jsp").forward(req,resp );
        }

    }
}


