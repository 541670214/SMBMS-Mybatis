package com.huihui.dao.provider;

import com.huihui.pojo.Provider;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-25 20:37
 * @Description:
 * @version: 1.0
 */
public interface ProviderMapper {
    //获取供应商列表通过供应商编码和供应商名称获取
    public List<Provider> getProvider(@Param("queryProCode") String queryProCode,@Param("queryProName") String queryProName);

    //根据id获取供应商方法
    public Provider getProviderId(@Param("id")Integer id);

    //根据id修改供应商
    public int getUpdateProvider(Provider provider);

    //根据id删除供应商
    public int Delprovider(@Param("id")Integer id);

    //添加供应商
    public int addProvider(Provider provider);
}
