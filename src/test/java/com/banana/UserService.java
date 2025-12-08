package com.banana;

import com.banana.content.UserEntity;
import com.tomato.sprout.anno.Autowired;
import com.tomato.sprout.anno.Component;
import org.apache.catalina.User;

import java.util.List;

@Component("userService")
public class UserService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserMapper userMapper;

    public void test() {
        List<UserEntity> userList = userMapper.getUserByName("sdfasd");
        for (UserEntity userEntity : userList) {
            System.out.println(userEntity.getUsername());
        }
    }

    public String testReturn(String title) {
        return "user testReturn" + title;
    }
}
