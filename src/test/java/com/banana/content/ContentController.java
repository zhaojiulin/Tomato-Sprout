package com.banana.content;

import com.tomato.sprout.anno.Autowired;
import com.tomato.sprout.constant.RequestMethod;
import com.tomato.sprout.web.anno.RequestBody;
import com.tomato.sprout.web.anno.WebController;
import com.tomato.sprout.web.anno.RequestParam;
import com.tomato.sprout.web.anno.WebRequestMapping;
import com.banana.UserService;
import com.tomato.sprout.web.model.ReqFile;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * @Description: Controller测试
 * @author zhaojiulin
 * @Date 2025/10/18 12:22
 * @version 1.0
 */
@WebController
@WebRequestMapping("/web")
public class ContentController {
    static final Logger logger = Logger.getLogger(ContentController.class.getName());
    @Autowired
    private UserService userService;
    @WebRequestMapping(value = "/test1", method = RequestMethod.GET)
    public void test1(String username) {
        userService.test();
    }
    @WebRequestMapping(value = "/test2", method = RequestMethod.GET)
    public Object test2(@RequestParam("username") String username) {
        return username;
    }

    @WebRequestMapping(value = "/test3", method = RequestMethod.GET)
    public Object test3(UserEntity user) {
        return user.getUsername();
    }

    @WebRequestMapping(value = "/test4", method = RequestMethod.POST)
    public Object test4(@RequestBody UserEntity user) {
        return user.getUsername();
    }

    @WebRequestMapping(value = "/test5", method = RequestMethod.POST)
    public Object test5(@RequestParam("file") ReqFile file) {
        return file.getFileBytes();
    }
    @WebRequestMapping(value = "/test6", method = RequestMethod.POST)
    public Object test6(@RequestParam("username") String username) {
        return username;
    }

    @WebRequestMapping(value = "/test7", method = RequestMethod.POST)
    public Object test7(@RequestBody UserEntity user) {
        return user.getUsername();
    }

    @WebRequestMapping(value = "/file", method = RequestMethod.POST)
    public void file(@RequestBody UserEntity user, HttpServletResponse response) {
        System.out.println(user.getFile().getFileName());
        Path path = Paths.get("d:"+File.separator + "hahahahtest" + File.separator + user.getFile().getFileName());
        try {
            // 写入本地
            Files.createDirectories(path.getParent());
            Files.copy(user.getFile().getInputStream(), path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
