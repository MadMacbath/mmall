package com.macbeth.service.impl;

import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.common.TokenCache;
import com.macbeth.dao.UserMapper;
import com.macbeth.pojo.User;
import com.macbeth.service.UserService;
import com.macbeth.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        User user = userMapper.getUserByUsername(username);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        if (user.getPassword().equals(MD5Util.MD5Encode(password))){
            user.setPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccess("登陆成功",user);
        }
        return ServerResponse.createByErrorMessage("密码错误");
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse response = this.checkValid(user.getUsername(),Constant.USERNAME);
        if (! response.isSuccess()){
            return response;
        }
        response = this.checkValid(user.getEmail(),Constant.EMAIL);
        if (! response.isSuccess()){
            return response;
        }
        user.setRole(Constant.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5Encode(user.getPassword()));
        int responseCount = userMapper.insert(user);
        if (responseCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        int responseCount;
        if (Constant.EMAIL.equalsIgnoreCase(type)){
            responseCount = userMapper.checkEmail(str);
            if (responseCount > 0)
                return ServerResponse.createByErrorMessage("邮箱已存在");
        }
        if (Constant.USERNAME.equalsIgnoreCase(type)){
            responseCount = userMapper.checkUserName(str);
            if (responseCount > 0)
                return ServerResponse.createByErrorMessage("用户名已存在");
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse response = this.checkValid(username,Constant.USERNAME);
        if (response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestion(username);
        return ServerResponse.createBySuccess(question);
    }

    @Override
    public ServerResponse<String> checkQuestionAnswer(String username, String question, String answer) {
        int responseCount = userMapper.queryQuestionAnswer(username,question,answer);
        if(responseCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setValue(Constant.TOKEN_PREFIX + username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("密保问题未通过");
    }

    @Override
    public ServerResponse<String> forgetRestPassword(String username, String password, String forgetToken) {
        ServerResponse response = this.checkValid(username,Constant.USERNAME);
        if (response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名：" + username + "不存在");
        }

        String token = TokenCache.getValue(Constant.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者已过期");
        }

        if (StringUtils.equals(forgetToken,token)){
            String passwordMD5 = MD5Util.MD5Encode(password);
            int responseCount = userMapper.updatePasswordByUsername(username,passwordMD5);
            if (responseCount == 0){
                return ServerResponse.createByErrorMessage("修改密码失败");
            }
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,User user) {
        int responseCount = userMapper.checkPassword(MD5Util.MD5Encode(passwordOld),user.getId());
        if (responseCount == 0){
            return ServerResponse.createByErrorMessage("重置密码失败,原密码错误");
        }
        user.setPassword(MD5Util.MD5Encode(passwordNew));
        responseCount = userMapper.updateByPrimaryKeySelective(user);
        if (responseCount == 0){
            return ServerResponse.createByErrorMessage("重置密码失败");
        }
        return ServerResponse.createBySuccessMessage("重置密码成功");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        int responseCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (responseCount > 0){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
        newUser.setQuestion(user.getQuestion());
        newUser.setAnswer(user.getAnswer());

        responseCount = userMapper.updateByPrimaryKeySelective(newUser);
        if (responseCount == 0){
            return ServerResponse.createByErrorMessage("更新用户信息失败");
        }
        return ServerResponse.createBySuccessMessage("更新用户信息成功");
    }

    @Override
    public ServerResponse<User> getInformation(Integer id) {
        User user = userMapper.getInformationByUserId(id);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
