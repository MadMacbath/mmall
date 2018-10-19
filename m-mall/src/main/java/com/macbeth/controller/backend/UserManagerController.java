package com.macbeth.controller.backend;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.User;
import com.macbeth.service.UserService;
import com.macbeth.to.manager.UserLoginManager;
import com.macbeth.util.CookieUtils;
import com.macbeth.util.JsonUtils;
import com.macbeth.util.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Api(tags = "管理员用户接口")
@RestController
@RequestMapping("managers")
public class UserManagerController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "管理员用户登陆")
    @RequestMapping(value = "user",method = RequestMethod.POST)
    public ServerResponse<User> login(@Valid @RequestBody UserLoginManager userLoginManager,
                                      @ApiIgnore HttpSession session,
                                      @ApiIgnore HttpServletResponse response){

        System.out.println("controller");
        ServerResponse<User> result = userService.login(userLoginManager.getUsername(),userLoginManager.getPassword());
        if (result.isSuccess()){
            User user = result.getData();
            if (user.getRole() == Constant.Role.ROLE_ADMIN){
                user.setPassword(StringUtils.EMPTY);
                String userStr = JsonUtils.obj2String(user);
                CookieUtils.writeLoginToken(response,session.getId());
                RedisUtils.setex(session.getId(),userStr,60 * 30);
                return result;
            }
            return ServerResponse.createByErrorMessage("登陆用户不是管理员");
        }
        return result;
    }
}
