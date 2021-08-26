package com.huihui.service.role;

import com.huihui.dao.role.RoleMapper;
import com.huihui.pojo.Role;
import com.huihui.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 14:14
 * @Description:
 * @version: 1.0
 */
public class RoleServiceImpl implements RoleService {
    //获取mybatis
    private static SqlSession sqlSession=null;
    private static RoleMapper mapper=null;
    static{
        sqlSession = MybatisUtils.getSqlSession();
        mapper = sqlSession.getMapper(RoleMapper.class);
    }


    @Override //获取角色列表
    public List<Role> getRoleList() {
        return mapper.getRoleList();
    }
}
