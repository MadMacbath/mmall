package com.macbeth.controller.backend;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.User;
import com.macbeth.service.UserService;
import com.macbeth.to.manager.UserLoginManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Api(tags = "管理员用户接口ce")
@RestController
@RequestMapping(value = "user-manager")
public class UserManagerController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "管理员用户登陆")
    @RequestMapping(value = "user",method = RequestMethod.POST)
    public ServerResponse<User> login(@Valid @RequestBody UserLoginManager userLoginManager,
                                      @ApiIgnore HttpSession session){

        ServerResponse<User> response = userService.login(userLoginManager.getUsername(),userLoginManager.getPassword());
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
