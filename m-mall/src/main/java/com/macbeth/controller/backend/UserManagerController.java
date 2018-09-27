package com.macbeth.controller.backend;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.User;
import com.macbeth.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manager")
public class UserManagerController {
    @Autowired
    private UserService userService;

    @PostMapping("user")
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = userService.login(username,password);
        if (response.isSuccess()){
            User user = response.getData();
            if (user.getRole() == Constant.Role.ROLE_ADMIN){
                user.setPassword(StringUtils.EMPTY);
                session.setAttribute(Constant.CURRENT_USER,user);
                return response;
            }
            return ServerResponse.createByErrorMessage("登陆用户不是管理员");
        }
        return response;
    }
}
