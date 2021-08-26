package com.huihui.dao.bill;

import com.huihui.pojo.Bill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 9:32
 * @Description:
 * @version: 1.0
 */
public interface BillMapper {
    //根据供应商id查询订单总数
    public int getBillProiderId(@Param("id") Integer id);

    //获取订单列表
    public List<Bill> getListBill(@Param("isPayment")Integer isPayment,@Param
            ("id") Integer id,@Param("productName") String productName);

    //根据id获取订单内容
    public Bill getBillId(@Param("id")Integer id);

    //根据id修改订单
    public int UpdateBill(Bill bill);

    //根据id删除订单
    public int deleteBill(@Param("id")int id);

    //添加订单
    public int addBill(Bill bill);
}
