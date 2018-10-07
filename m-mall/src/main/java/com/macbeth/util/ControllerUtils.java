package com.macbeth.util;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.User;

import javax.servlet.http.HttpSession;

public class ControllerUtils {
    public static ServerResponse<User> isLogin(HttpSession session){
        Object object = session.getAttribute(Constant.CURRENT_USER);
        if (object == null)
            return ServerResponse.createByErrorMessage("用户未登陆");
        User user = (User) object;
        return ServerResponse.createBySuccess(user);
    }

    public static ServerResponse<User> isAdminLogin(ServerResponse response){
        User user = (User) response.getData();
        if (user.getRole() != Constant.Role.ROLE_ADMIN)
            return ServerResponse.createByErrorMessage("当前用户不是管理员");
        return response;
    }
}
