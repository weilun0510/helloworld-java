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
    public List<User> query() {
        List<User> list = userMapper.selectList();
        // 使用 BaseMapper 提供的内置方法
        // List<User> list = userMapper.selectList(null);
        System.out.println(list);
        return list;
    }

    @GetMapping("/user/findAll")
    public List<User> findAll() {
        return userMapper.selectAllUserAndOrders();
    }

    @PostMapping("/user")
    public String save(@RequestBody User user) {

        int i = userMapper.insert(user);
        System.out.println("插入后，数据库生成的ID: " + user.getId());

        if (i > 0) {
            return "插入成功，生成的ID为: " + user.getId();
        } else {
            return "插入失败";
        }
    }

    // 获取动态参数
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable int id) {
        System.out.println(id);
        return "根据ID获取用户信息";
    }

    // @DeleteMapping("/user/{id}")
    // public String delete(@PathVariable int id) {
    // int i = userMapper.deleteById(id);
    // System.out.println(i);
    // if(i>0){
    // return "删除成功";
    // } else {
    // return "删除失败";
    // }
    // }
    //
    // @PutMapping("/user/{id}")
    // public String update(@PathVariable int id, @RequestBody User user) {
    //// user.setId(id); // 确保 ID 被设置进去
    // int result = userMapper.updateById(user);
    // return result > 0 ? "更新成功" : "更新失败";
    // }
}
