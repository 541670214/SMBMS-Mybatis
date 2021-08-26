package com.huihui.dao.role;

import com.huihui.pojo.Role;
import java.util.List;

/**
 * @Author: 辉辉
 * @Date: 2021-08-26 19:57
 * @Description:
 * @version: 1.0
 */

public interface RoleMapper {
    //获取角色列表
    public List<Role> getRoleList();
}

