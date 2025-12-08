package com.tomato.sprout.web.servlet;

import com.tomato.sprout.constant.RequestMethod;
import com.tomato.sprout.web.mapping.request.AbstractHandleMapping;
import com.tomato.sprout.web.mapping.request.HandleMappingFactory;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author zhaojiulin
 * @version 1.0
 * @Description: 前端控制器
 * 负责处理HTTP请求处理
 * 参数解析
 * 响应数据转换
 * @Date 2025/10/18 12:56
 */
public class DispatcherServlet extends HttpServlet {
    /**
     * @description: 请求处理
     * @author zhaojiulin
     * @param: null
     * @return:
     * @Date: 2025/10/18 12:57
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        String reqMethod = req.getMethod();
        AbstractHandleMapping handleMapping = HandleMappingFactory.handleMapping(RequestMethod.valueOf(reqMethod));
        handleMapping.doHandleMapping(req, resp);
    }

}
