package org.example.helloworld.controller;

import lombok.Data;
import org.example.helloworld.entity.User;
import org.springframework.web.bind.annotation.*;

@Data
@RestController
public class UserController {
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable int id) {
        System.out.println(id);
        return "根据ID获取用户信息";
    }

    @PostMapping("/user")
    public String save(@RequestBody User user) {
        System.out.println(user);
        return "新增用户";
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
