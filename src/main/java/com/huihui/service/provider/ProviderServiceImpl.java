package com.huihui.service.provider;

import com.huihui.dao.provider.ProviderMapper;
import com.huihui.pojo.Provider;
import com.huihui.service.bill.BillServiceImpl;
import com.huihui.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-25 20:41
 * @Description:
 * @version: 1.0
 */
public class ProviderServiceImpl implements ProviderService{
    //获取mybatis
    private static SqlSession sqlSession=null;
    private static ProviderMapper mapper=null;
    static{
        sqlSession = MybatisUtils.getSqlSession();
        mapper = sqlSession.getMapper(ProviderMapper.class);
    }

    //获取供应商列表通过供应商编码和供应商名称获取
    @Override
    public List<Provider> getProvider(String queryProCode, String queryProName) {
        return mapper.getProvider(queryProCode, queryProName);
    }

    //根据id获取供应商方法
    @Override
    public Provider getProviderId(Integer id) {
        return mapper.getProviderId(id);
    }

    //根据id修改供应商信息
    @Override
    public int getUpdateProvider(Provider provider) {
        return mapper.getUpdateProvider(provider);
    }

    //根据id删除供应商
    @Override
    public int Delprovider(Integer id) {
        BillServiceImpl billService = new BillServiceImpl();
        int billProiderId = billService.getBillProiderId(id);//调用根据供应商id查询订单总数方法
        if (billProiderId>0){//判断订单总数大于0还有订单数不能删除直接return 订单数
            return billProiderId;
        }else {//如果订单总数不大于0就代表没有订单数 则可以调用删除供应商方法删除成功返回0删除失败返回-1
            int delprovider = mapper.Delprovider(id);
            if (delprovider>0){
                return 0;
            }else {
                return -1;
            }
        }

    }

    //添加供应商
    @Override
    public int addProvider(Provider provider) {
        return mapper.addProvider(provider);
    }
}
