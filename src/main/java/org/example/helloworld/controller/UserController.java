package org.example.helloworld.controller;

import lombok.Data;
import org.example.helloworld.entity.User;
import org.example.helloworld.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RestController
public class UserController {

    // 通过@Autowired注解，spring框架会自动注入指定的mapper实例化出来的对象，并赋值给声明的属性
    // 如果没加入注解，userMapper为空
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/user")
    public String query(){
        List<User> list = userMapper.find();
        System.out.println(list);
        return "user";
    }


    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable int id) {
        System.out.println(id);
        return "根据ID获取用户信息";
    }

    @PostMapping("/user")
    public String save(@RequestBody User user) {
//        System.out.println(user);
        int i = userMapper.insert(user);
        if(i>0){
            return "插入成功";
        } else {
            return "插入失败";
        }
    }

    @DeleteMapping("/user/{id}")
    public String delete(@PathVariable int id) {
        System.out.println(id);
        return "删除用户";
    }

    @PutMapping("/user")
    public String update(@RequestBody User user) {
        System.out.println(user);
        return "更新用户";
    }
}
