package com.banana.content;

import com.tomato.sprout.web.model.ReqFile;

import java.util.List;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: TODO
 * @date 2025/10/22 0:37
 */
public class UserEntity {
    private String username;
    private Integer age;

    private ReqFile file;

    private List<String> stringList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public ReqFile getFile() {
        return file;
    }

    public void setFile(ReqFile file) {
        this.file = file;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
