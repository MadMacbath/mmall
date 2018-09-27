package com.macbeth.dao;

import com.macbeth.pojo.User;
import com.macbeth.pojo.UserExample;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    User getUserByUsername(String username);

    int checkEmail(String email);

    String selectQuestion(String username);

    int queryQuestionAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("password") String passwordMD5);

    int checkPassword(@Param("password") String password, @Param("id") Integer id);

    int checkEmailByUserId(@Param("email") String email, @Param("id") Integer id);

    User getInformationByUserId(Integer id);
}