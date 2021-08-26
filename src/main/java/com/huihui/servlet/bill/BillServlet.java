package com.huihui.servlet.bill;

import com.alibaba.fastjson.JSONArray;
import com.huihui.pojo.Bill;
import com.huihui.pojo.Provider;
import com.huihui.pojo.User;
import com.huihui.service.bill.BillServiceImpl;
import com.huihui.service.provider.ProviderServiceImpl;
import com.huihui.utils.Constants;
import com.mysql.jdbc.StringUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 10:36
 * @Description:
 * @version: 1.0
 */
public class BillServlet extends HttpServlet {
    @Override//get请求
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //通过判断method参数的值实现复用
        String method = req.getParameter("method");
        if(method!=null&&method.equals("query")){//获取method的值等于query就是调用获取订单列表方法
            this.query(req,resp);
        }else if(method!=null&&method.equals("view")) {//获取method的值等于view就是调用根据id获取订单信息方法
            this.view(req,resp,"billview.jsp");
        }else if (method!=null&&method.equals("modify")) {//获取method的值等于modify就是调用根据id获取订单信息方法(用于显示值把值删除就可以修改了)
            this.modify(req,resp,"billmodify.jsp");
        }else if(method!=null&&method.equals("getproviderlist")) {//获取method的值等于getproviderlist就是调用获取供应商列表方法（用于修改页面下拉框选择供应商）
            this.getproviderlist(req,resp);
        }else if(method!=null&&method.equals("modifysave")) {//获取method的值等于modifysave就是调用修改订单方法
            this.modifysave(req,resp);
        }else if(method!=null&&method.equals("delbill")) {//获取method的值等于delbill就是调用删除订单方法
            this.delbill(req,resp);
        }else if(method!=null&&method.equals("add")){//获取method的值等于add就是调用添加订单方法
            this.add(req,resp);
        }
    }

    @Override//如果是post请求调用get请求
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //查询订单列表
    private void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryProductName = req.getParameter("queryProductName");//商品名称
        String queryProviderId = req.getParameter("queryProviderId");//供应商id
        String queryIsPayment = req.getParameter("queryIsPayment");//是否付款

        if(StringUtils.isNullOrEmpty(queryProductName)) {//判断商品名称是否为空如果为空就设置为空字符串方便sql查询全部 %%
            queryProductName="";
        }
        if(StringUtils.isNullOrEmpty(queryProviderId)) {//判断供应商id是否为空如果为空就设置为0在sql查询时有判断如果供应商id为0就不做供应商查询约束
            queryProviderId="0";
        }
        if(StringUtils.isNullOrEmpty(queryIsPayment)) {//判断是否付款是否为空如果为空就设置为0为查询全部1为查询未付款2为已付款
            queryIsPayment="0";
        }

        BillServiceImpl billService = new BillServiceImpl();
        //调用查询订单方法传入是否付款，供应商id，商品名称
        List<Bill> listBill = billService.getListBill(Integer.parseInt(queryIsPayment),Integer.parseInt(queryProviderId) , queryProductName);

        //调用查询供应商方法返回全部供应商 用户前端下拉框获取所有供应商名称
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        List<Provider> provider = providerService.getProvider("", "");

        req.setAttribute("providerList",provider );//把供应商信息传入前端
        req.setAttribute("billList", listBill);//把订单列表传入前端
        req.setAttribute("queryProductName", queryProductName);//把选中的商品名称传入前端
        req.setAttribute("queryProviderId", queryProviderId);//把选中的商品id
        req.setAttribute("queryIsPayment", queryIsPayment);//把选中的是否付款传入前端
        //页面转发到查询订单列表
        req.getRequestDispatcher("billlist.jsp").forward(req,resp );
    }

    //根据id查询订单
    private void view(HttpServletRequest req, HttpServletResponse resp,String url) throws ServletException, IOException {
        String id = req.getParameter("billid");//获取订单id
        if(!StringUtils.isNullOrEmpty(id)){//判断id不为空
            BillServiceImpl billService = new BillServiceImpl();
            Bill bill = billService.getBillId(Integer.parseInt(id));//调用查询方法传入id
            req.setAttribute("bill", bill);//把查询结果传入到前端
            req.getRequestDispatcher(url).forward(req,resp );//页面转发
        }
    }

    //显示修改页面的数据根据id查询订单
    private void modify(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        view(req,resp,url);//调用根据id查询订单方法传入修改页面url
    }

    //获取供应商列表用于修改页面下拉框选择供应商
    private void getproviderlist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        //调用方法获取供应商列表
        List<Provider> provider = providerService.getProvider("", "");

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(provider));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //调用修改订单方法
    private void modifysave(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");//获取要修改的id
        String billCode = req.getParameter("billCode");//获取商品编码
        String productName = req.getParameter("productName");//获取商品名称
        String productUnit = req.getParameter("productUnit");//获取商品单位
        String productCount = req.getParameter("productCount");//获取商品数量
        String totalPrice = req.getParameter("totalPrice");//获取总金额
        String providerId = req.getParameter("providerId");//获取供应商ID
        String isPayment = req.getParameter("isPayment");//获取是否支付

        Bill bill = new Bill();
        bill.setId(Integer.parseInt(id));//设置修改的订单id
        bill.setBillCode(billCode);//设置商品编码
        bill.setProductName(productName);//设置商品名称
        bill.setProductUnit(productUnit);//设置商品单位
        bill.setProductCount(new BigDecimal(productCount));//设置商品数量
        bill.setTotalPrice(new BigDecimal(totalPrice));//设置商品总金额
        bill.setProviderId(Integer.parseInt(providerId));//设置供应商id
        bill.setIsPayment(Integer.parseInt(isPayment));//设置是否付款
        bill.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());//设置更新者id
        bill.setModifyDate(new Date());//设置跟新时间

        BillServiceImpl billService = new BillServiceImpl();
        int i = billService.UpdateBill(bill);//调用修改方法
        if(i>0){//修改成功
            resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }else {//修改视频
            req.getRequestDispatcher("billmodify.jsp").forward(req,resp );
        }
    }

    //调用删除订单方法
    private void delbill(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("billid");//获取要删除的订单id
        HashMap<String, String> reultMap = new HashMap<>();//创建一个map集合用于返回数据到前端
        if(!StringUtils.isNullOrEmpty(id)){//判断id不等于空

            BillServiceImpl billService = new BillServiceImpl();
            int i = billService.deleteBill(Integer.parseInt(id));//调用删除方法
            if(i>0){//判断是否删除成功
                reultMap.put("delResult", "true");//成功返回true
            }else {
                reultMap.put("delResult", "false");//失败返回false
            }
        }else {
            reultMap.put("delResult", "notexist");//id不存在返回notexist
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(reultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //调用添加订单方法
    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String billCode = req.getParameter("billCode");//获取商品编码
        String productName = req.getParameter("productName");//获取商品名称
        String productUnit = req.getParameter("productUnit");//获取商品单位
        String productCount = req.getParameter("productCount");//获取商品数量
        String totalPrice = req.getParameter("totalPrice");//获取商品总金额
        String providerId = req.getParameter("providerId");//获取供应商id
        String isPayment = req.getParameter("isPayment");//获取是否付款

        Bill bill = new Bill();
        bill.setBillCode(billCode);//设置商品编码
        bill.setProductName(productName);//设置商品名称
        bill.setProductUnit(productUnit);//设置商品单位
        bill.setProductCount(new BigDecimal(productCount));//设置商品数量
        bill.setTotalPrice(new BigDecimal(totalPrice));//设置商品总金额
        bill.setProviderId(Integer.parseInt(providerId));//设置供应商id
        bill.setIsPayment(Integer.parseInt(isPayment));//设置是否付款
        bill.setCreatedBy(((User)req.getSession().getAttribute(Constants
                .USER_SESSION)).getId());//设置创建者id
        bill.setCreationDate(new Date());//设置创建时间


        BillServiceImpl billService = new BillServiceImpl();
        int i = billService.addBill(bill);//调用添加订单方法
        if(i>0){//添加成功页面重定向到查询订单页面
            resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }else {//添加失败页面转发到添加页面
            req.getRequestDispatcher("billadd.jsp").forward(req,resp );
        }

    }

}

