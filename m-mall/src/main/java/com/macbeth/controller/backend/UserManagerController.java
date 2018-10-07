package com.macbeth.controller.backend;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.User;
import com.macbeth.service.UserService;
import com.macbeth.to.manager.UserLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Api(tags = "管理员用户接口")
@RestController
@RequestMapping("/manager")
public class UserManagerController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "管理员用户登陆")
    @PostMapping("user")
    public ServerResponse<User> login(@Valid UserLogin userLogin,
                                      @ApiIgnore HttpSession session){

        ServerResponse<User> response = userService.login(userLogin.getUsername(),userLogin.getPassword());
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
