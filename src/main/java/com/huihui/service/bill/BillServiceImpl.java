package com.huihui.service.bill;

import com.huihui.dao.bill.BillMapper;
import com.huihui.pojo.Bill;
import com.huihui.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 9:39
 * @Description:
 * @version: 1.0
 */
public class BillServiceImpl implements BillService {
    //获取mybatis
    private static SqlSession sqlSession=null;
    private static BillMapper mapper=null;
    static{
        sqlSession = MybatisUtils.getSqlSession();
        mapper = sqlSession.getMapper(BillMapper.class);
    }

    //根据供应商id查询订单总数
    @Override
    public int getBillProiderId(Integer id) {
        return mapper.getBillProiderId(id);
    }

    //获取订单列表
    @Override
    public List<Bill> getListBill(Integer isPayment, Integer id, String productName) {
        return mapper.getListBill(isPayment,id ,productName );
    }

    //根据id获取订单
    @Override
    public Bill getBillId(Integer id) {
        return mapper.getBillId(id);
    }

    //根据id修改订单
    @Override
    public int UpdateBill(Bill bill) {
        return mapper.UpdateBill(bill);
    }

    //根据id删除订单
    @Override
    public int deleteBill(int id) {
        return mapper.deleteBill(id);
    }

    //添加订单
    @Override
    public int addBill(Bill bill) {
        return mapper.addBill(bill);
    }


}
