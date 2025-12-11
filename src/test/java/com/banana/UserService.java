package com.banana;

import com.banana.content.UserEntity;
import com.tomato.sprout.anno.Autowired;
import com.tomato.sprout.anno.Component;
import com.tomato.sprout.orm.transaction.BaseTransactionalService;
import org.apache.catalina.User;

import java.util.List;

@Component("userService")
public class UserService extends BaseTransactionalService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserMapper userMapper;

    public void test() {
        try {
            executeQuery(()-> {
                List<UserEntity> userList = userMapper.getUserByName("sdfasd");
                for (UserEntity userEntity : userList) {
                    System.out.println(userEntity.getUsername());
                }
                return userList;
            });
            executeTransactionalVoid(()-> {
                userMapper.updateAge(1, 15);
                userMapper.updateName(1, "大美女");
                // 发生异常
                int a = 1/0;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public String testReturn(String title) {
        return "user testReturn" + title;
    }
}
