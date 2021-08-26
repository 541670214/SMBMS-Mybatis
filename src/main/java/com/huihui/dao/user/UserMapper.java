package com.huihui.dao.user;

import com.huihui.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 16:57
 * @Description:
 * @version: 1.0
 */

public interface UserMapper {
    public User getId(int id);
    //用户登录判断
    public User getUser(@Param("user") String user , @Param("pwd") String pwd);
    //用户修改密码
    public int setUpdatePwd(@Param("id")int id,@Param("pwd")String pwd);

    //查询用户总数(根据用户名或者角色查询)
    public int getUserCount(@Param("username")String name,@Param("userRole") int userRole);

    //获取用户列表
    public List<User> getUserList(@Param("username")String name,@Param
            ("userRole") int userRole,@Param("currentPageNo")int currentPageNo,@Param("pageSize")int pageSize);

    //根据id查询列表
    public User getUserId(@Param("id")int id);

    //根据id修改用户信息
    public int setUpdateUser(User user);

    //根据id删除用户
    public int setDeleteUser(@Param("id")int id);

    //添加用户
    public int addUser(User user);

    //通过userCode判断是否有账号同样的不允许添加
    public int getUserCode(@Param("userCode")String userCode);
}
