package com.huihui.service.user;

import com.huihui.dao.user.UserMapper;
import com.huihui.pojo.User;
import com.huihui.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 15:37
 * @Description:
 * @version: 1.0
 */
public class UserServiceImpl implements UserService {
    //获取mybatis
     private static SqlSession sqlSession =null;
     private static UserMapper mapper=null;
    static{
        sqlSession = MybatisUtils.getSqlSession();
        mapper = sqlSession.getMapper(UserMapper.class);
    }

    @Override//登录
    public  User login(String user, String pwd) {
        return mapper.getUser(user, pwd);
    }

    @Override//修改用户密码
    public Boolean updatePwd(int id, String pwd) {
        if(mapper.setUpdatePwd(id,pwd)>0){
            return true;
        }
        return false;
    }

    @Override//查询用户总数（可以根据user名称和角色进行查询）
    public int getUserCount(String name, int userRole) {
        return mapper.getUserCount(name, userRole);
    }

    @Override//根据条件查询用户列表
    public List<User> getUserList(String name, int userRole, int currentPageNo, int pageSize) {
        return mapper.getUserList(name, userRole, (currentPageNo-1)*pageSize, pageSize);
    }

    @Override//根据id查询用户
    public User getUserId(int id) {
        return mapper.getUserId(id);
    }

    @Override//根据id修改用户数据
    public int setUpdateUser(User user) {
        return mapper.setUpdateUser(user);
    }

    @Override//根据id删除用户
    public int setDeleteUser(int id) {
        return mapper.setDeleteUser(id);
    }

    @Override//添加用户
    public int addUser(User user) {
        return mapper.addUser(user);
    }

    @Override//判断用户名是否存在返回boolean值
    public Boolean getUserCode(String userCode) {
        int userCode1 = mapper.getUserCode(userCode);
        if(userCode1>0){
            return true;
        }
        return false;
    }
}
