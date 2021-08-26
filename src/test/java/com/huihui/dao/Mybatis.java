package com.huihui.dao;

import com.huihui.dao.provider.ProviderMapper;
import com.huihui.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;


public class Mybatis {

    @Test//测试
    public void mybatis(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        ProviderMapper mapper = sqlSession.getMapper(ProviderMapper.class);
        mapper.getProvider("","");

    }



}
