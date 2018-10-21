package com.macbeth.controller.portal;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.User;
import com.macbeth.service.UserService;
import com.macbeth.util.*;
import com.macbeth.to.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Api(tags = "用户接口")
@Slf4j
public class UserController {


    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户登陆")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(value = "用户名",name = "username",paramType = "path")
    })
    @PostMapping(value = "user/{username}/session")
    public ServerResponse<User> login(@PathVariable("username") String username,
                                      @Valid @RequestBody UserLogin user,
                                      @ApiIgnore HttpSession session,
                                      @ApiIgnore HttpServletResponse response){

        ServerResponse result = userService.login(username, user.getPassword());
        if (result.isSuccess()) {
            CookieUtils.writeLoginToken(response,session.getId());
            String userSerializedString = JsonUtils.obj2String(result.getData());
            RedisUtils.setex(session.getId(),userSerializedString,60 * 30);
        }
        return result;
    }
    @ApiOperation(value = "用户注销")
    @DeleteMapping("user/session")
    public ServerResponse<String> logOut(@ApiIgnore HttpServletRequest request,
                                         @ApiIgnore HttpServletResponse response){

        String tokenKey = CookieUtils.readLoginToken(request);
        RedisUtils.del(tokenKey);
        CookieUtils.delLoginToken(response,Constant.COOKIE_NAME,request);
        return ServerResponse.createBySuccess();
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("user")
    public ServerResponse<String> register(@Valid @RequestBody UserRegister userRegister){
        log.info("用户注册");
        User user = new User();
        ObjectUtils.transferEntity(user,userRegister);
        return userService.register(user);
    }

    @ApiOperation(value = "新增数据时用来校验用户名和邮箱唯一性的校验接口")
    @GetMapping("validator")
    public ServerResponse<String> checkValid(@Valid  UserValidator userValidator){

        return userService.checkValid(userValidator.getStr(),userValidator.getType());
    }

    @ApiOperation(value = "获取当前登陆用户的详细信息")
    @GetMapping("user/session")
    public ServerResponse<User> getUserInfo(@ApiIgnore HttpServletRequest request){

        ServerResponse response = ControllerUtils.isLogin(request);
        if (response.isError())
            return ServerResponse.createByErrorMessage("用户未登陆");

        User user = (User) response.getData();
        return ServerResponse.createBySuccess(user);
    }

    @ApiOperation(value = "用户密保问题")
    @GetMapping("user/{username}/question")
    public ServerResponse<String> forgetPasswordQuestion(@PathVariable @ApiParam(name = "username", value = "用户名") String username){

        return userService.selectQuestion(username);
    }

    @ApiOperation(value = "验证用户密保答案")
    @PostMapping("user/{username}/token")
    public ServerResponse<String> forgetCheckAnswer(@PathVariable @ApiParam(name = "username",value = "用户名") String username,
                                                    @Valid  UserQuestionAnwser userQuestionAnwser){

        return this.userService.checkQuestionAnswer(username,userQuestionAnwser.getQuestion(),userQuestionAnwser.getAnswer());
    }

    @ApiOperation(value = "修改密码")
    @PutMapping("user/{username}/password")
    public ServerResponse<String> forgetResetPassword(@PathVariable @ApiParam(name = "username",value = "用户名") String username,
                                                      @Valid  UserRestPasswordToken userRestPasswordToken){

        return userService.forgetRestPassword(username,userRestPasswordToken.getPassword(),userRestPasswordToken.getToken());
    }

    @ApiOperation(value = "登陆状态修改密码")
    @PutMapping("user/password/session")
    public ServerResponse<String> resetPassword(@ApiIgnore HttpServletRequest request,
                                                @Valid  UserRestPasswordSession userRestPasswordSession){

        ServerResponse response = ControllerUtils.isLogin(request);
        if (response.isError())
            return ServerResponse.createByErrorMessage("用户未登陆");

        User user = (User) response.getData();
        return userService.resetPassword(userRestPasswordSession.getPasswordOld(),userRestPasswordSession.getPasswordNew(),user);
    }

    @ApiOperation(value = "更新用户信息")
    @PutMapping("user")
    public ServerResponse<User> updateInformation(@ApiIgnore HttpSession session,
                                                  @Valid @RequestBody  UserUpdate userUpdate){

        Object object = session.getAttribute(Constant.CURRENT_USER);
        if (object == null) return ServerResponse.createByErrorMessage("用户未登陆");
        User currentUser = (User) object;
        ObjectUtils.transferEntity(currentUser,userUpdate);
        return userService.updateInformation(currentUser);
    }

//    // 获取当前登陆用户详细信息
//    @GetMapping("user")
//    public ServerResponse<User> getInformation(HttpSession session){
//        Object object = session.getAttribute(Constant.CURRENT_USER);
//        if (object == null){
//            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
//        }
//        User user = (User) object;
//        return userService.getInformation(user.getId());
//    }

}
