package com.banana.content;

import com.tomato.sprout.anno.Autowired;
import com.tomato.sprout.constant.RequestMethod;
import com.tomato.sprout.web.anno.RequestBody;
import com.tomato.sprout.web.anno.WebController;
import com.tomato.sprout.web.anno.RequestParam;
import com.tomato.sprout.web.anno.WebRequestMapping;
import com.banana.UserService;
import com.tomato.sprout.web.model.ReqFile;
import jakarta.servlet.ServletOutputStream;
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
    @WebRequestMapping(value = "/info", method = RequestMethod.POST)
    public Object index(@RequestBody UserEntity user) {
        return user.getStringList();
    }

    @WebRequestMapping(value = "/hello", method = RequestMethod.GET)
    public Object hello(UserEntity user) {
       return user.getUsername();
    }
    @WebRequestMapping(value = "/testReturn", method = RequestMethod.POST)
    public String testReturn(@RequestParam("title") String title) {
        return userService.testReturn(title);
    }

    @WebRequestMapping(value = "/file", method = RequestMethod.POST)
    public void file(UserEntity user, HttpServletResponse response) {
        System.out.println(user.getFile().getFileName());
        Path path = Paths.get("d:"+File.separator + "hahahahtest" + File.separator + user.getFile().getFileName());
        try {
            // 写入本地
            Files.createDirectories(path.getParent());
            Files.copy(user.getFile().getInputStream(), path);

             // 响应数据流
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = user.getFile().getInputStream().read(buffer)) != -1) {
//                response.getOutputStream().write(buffer, 0, bytesRead);
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
