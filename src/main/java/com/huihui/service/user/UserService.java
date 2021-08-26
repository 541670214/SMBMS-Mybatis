package com.huihui.service.user;
import com.huihui.pojo.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 15:36
 * @Description:
 * @version: 1.0
 */
public interface UserService {
    //用户登录
    public User login(@Param("user") String user , @Param("pwd") String pwd);

    //密码修改
    public Boolean updatePwd(@Param("id")int id,@Param("pwd")String pwd);

    //获取列表总数（根据名称模糊查询或者角色）
    public int getUserCount(@Param("username")String name,@Param("userRole") int userRole);

    //根据条件查询用户列表
    public List<User> getUserList(@Param("username")String name, @Param("userRole") int userRole, @Param("currentPageNo")int currentPageNo, @Param("pageSize")int pageSize);

    //根据id查询列表
    public User getUserId(@Param("id")int id);

    //根据id修改用户信息
    public int setUpdateUser(User user);

    //根据id删除用户
    public int setDeleteUser(@Param("id")int id);

    //添加用户
    public int addUser(@Param("user") User user);

    //通过userCode判断是否有账号同样的不允许添加
    public Boolean getUserCode(@Param("userCode")String userCode);
}
