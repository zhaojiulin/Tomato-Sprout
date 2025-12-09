package com.banana;

import com.tomato.sprout.TomatoApplicationContext;
import com.tomato.sprout.anno.TomatoBoot;
import com.tomato.sprout.TomatoBootApplication;

@TomatoBoot
public class Main {
    public static void main(String[] args) {
        TomatoApplicationContext tomatoApplicationContext = TomatoBootApplication.startContext(Main.class, args);
        System.out.println(tomatoApplicationContext.getBean("userService"));
        System.out.println(tomatoApplicationContext.getBean("contentService"));
    }
}