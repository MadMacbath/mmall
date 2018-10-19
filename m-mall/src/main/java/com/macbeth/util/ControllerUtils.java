package com.macbeth.util;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public class ControllerUtils {

    public static ServerResponse<User> isLogin(HttpServletRequest request){

        String key = CookieUtils.readLoginToken(request);
        String userStr = StringUtils.isEmpty(key) ? "" : RedisUtils.get(key);
        if (StringUtils.isEmpty(userStr))
            return ServerResponse.createByErrorMessage("用户未登录");

        RedisUtils.expire(key,60 * 30);
        return ServerResponse.createBySuccess(JsonUtils.string2Obj(userStr, new TypeReference<User>() {}));
    }

    public static ServerResponse<User> isAdminLoginNew(ServerResponse response){

        User user = (User) response.getData();
        if (user.getRole() != Constant.Role.ROLE_ADMIN)
            return ServerResponse.createByErrorMessage("当前用户不是管理员");
        return response;
    }

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
