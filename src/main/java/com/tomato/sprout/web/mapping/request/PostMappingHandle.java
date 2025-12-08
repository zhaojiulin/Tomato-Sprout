package com.tomato.sprout.web.mapping.request;

import com.tomato.sprout.constant.HttpContentType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: POST请求
 * @date 2025/10/21 23:13
 */
public class PostMappingHandle extends AbstractHandleMapping {
    @Override
    public HashMap<String, Object> doParam(HttpServletRequest request) {
        HashMap<String, Object> paramMap = new HashMap<>();
        String contentType = request.getContentType();
        if (HttpContentType.FORM_DATA.getValue().equals(contentType)) {
            // 处理multipart/form-data（包含文件上传）
            try {
                // 使用Servlet的Part API处理文件上传
                Collection<Part> parts = request.getParts();
                for (Part part : parts) {
                    String fieldName = part.getName();
                    System.out.println(fieldName);
                    if (part.getContentType() != null) {
                        // 获取文件名
                        String fileName = part.getSubmittedFileName();
                        if (fileName != null && !fileName.isEmpty()) {
                            // 读取文件内容到字节数组
                            InputStream fileContent = part.getInputStream();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int bytesRead;

                            while ((bytesRead = fileContent.read(buffer)) != -1) {
                                baos.write(buffer, 0, bytesRead);
                            }

                            byte[] fileBytes = baos.toByteArray();

                            // 创建文件信息对象
                            Map<String, Object> fileInfo = new HashMap<>();
                            fileInfo.put("fileName", fileName);
                            fileInfo.put("fileSize", part.getSize());
                            fileInfo.put("contentType", part.getContentType());
                            fileInfo.put("fileBytes", fileBytes);
                            fileInfo.put("originalPart", part); // 可以存储原始Part对象，用于后续处理

                            paramMap.put(fieldName, fileInfo);
                            System.out.println("文件保存成功: " + fileName + " (" + part.getSize() + " bytes)");
                        }
                    } else {
                        // 这是普通文本字段
                        String fieldValue = readPartAsString(part);
                        paramMap.put(fieldName, fieldValue);
                        System.out.println("普通字段: " + fieldName + " = " + fieldValue);
                    }
                }
            } catch (IOException | ServletException e) {
                throw new RuntimeException("处理multipart/form-data失败", e);
            }
        } else {
            // 2. 处理普通application/x-www-form-urlencoded表单参数
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                paramMap.put(paramName, paramValue);
                System.out.println("普通参数: " + paramName + " = " + paramValue);
            }
        }
        // 出json
        try {
            BufferedReader reader = request.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            if (reader.ready()) {
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                if (!jsonBuilder.toString().isEmpty()) {
                    String jsonBody = jsonBuilder.toString();
                    System.out.println("原始JSON: " + jsonBody);
                    paramMap.put("arg0", jsonBody);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paramMap;

    }

    private String readPartAsString(Part part) throws IOException {
        try (InputStream inputStream = part.getInputStream();
             ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8);
        }
    }
}
