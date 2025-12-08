package com.banana;


import com.banana.content.UserEntity;
import com.tomato.sprout.orm.anno.RepoExec;
import com.tomato.sprout.orm.anno.RepoMapper;
import com.tomato.sprout.orm.anno.RepoParam;

import java.util.List;

@RepoMapper
public interface UserMapper {
    @RepoExec("select * from user_test")
    public List<UserEntity> getUserList();

    @RepoExec("select * from user_test where username = #{username}")
    public List<UserEntity> getUserByName(@RepoParam("username") String username);

    @RepoExec("select * from user_test where username = #{username}")
    public UserEntity getUser(@RepoParam("username") String username);

    @RepoExec("update user_test set age = #{age} where username = #{username}")
    public Integer updateAge(@RepoParam("username") String username, @RepoParam("age") Integer age);
}
