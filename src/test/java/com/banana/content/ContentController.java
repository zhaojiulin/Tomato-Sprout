package com.banana.content;

import com.tomato.sprout.anno.Autowired;
import com.tomato.sprout.constant.RequestMethod;
import com.tomato.sprout.web.anno.RequestBody;
import com.tomato.sprout.web.anno.WebController;
import com.tomato.sprout.web.anno.RequestParam;
import com.tomato.sprout.web.anno.WebRequestMapping;
import com.banana.UserService;

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
    @WebRequestMapping(value = "/info", method = RequestMethod.GET)
    public Object index(@RequestParam("title") String title, @RequestParam("age") Integer age) {
        logger.info("index：" + title);
        logger.info("age：" + age);
        return title;
    }

    @WebRequestMapping(value = "/hello", method = RequestMethod.POST)
    public void hello(Integer title, String content) {
        userService.test();
    }
    @WebRequestMapping(value = "/testReturn", method = RequestMethod.POST)
    public String testReturn(@RequestParam("title") String title) {
        return userService.testReturn(title);
    }
}
