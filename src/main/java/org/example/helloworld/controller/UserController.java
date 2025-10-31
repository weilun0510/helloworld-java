package org.example.helloworld.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.example.helloworld.entity.User;
import org.example.helloworld.mapper.UserMapper;
import org.example.helloworld.utils.JwtUtil;
import org.example.helloworld.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

@Data
@RequestMapping("/user")
@RestController
public class UserController {

    // 通过@Autowired注解，spring框架会自动注入指定的mapper实例化出来的对象，并赋值给声明的属性
    // 如果没加入注解，userMapper为空
    @Autowired
    private UserMapper userMapper;

    // @GetMapping("")
    // public List<User> query() {
    // List<User> list = userMapper.selectList();
    // // 使用 BaseMapper 提供的内置方法
    // // List<User> list = userMapper.selectList(null);
    // System.out.println(list);
    // return list;
    // }

    // @GetMapping("/findAll")
    // public List<User> findAll() {
    // return userMapper.selectAllUserAndOrders();
    // }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        String token = JwtUtil.generateToken(user.getUsername());
        System.out.println("token:" + token);
        if (token != null) {
            return Result.ok().data("token", token);
        } else {
            return Result.error().message("登录失败");
        }
    }

    // ------------------ baseMapper 提供的内置方法 ------------------

    // 条件查询
    @GetMapping("/findByUsername")
    public List<User> findByUsername(@RequestParam(value = "username", required = false) String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        if (username != null) {
            queryWrapper.eq("username", username);
        }
        return userMapper.selectList(queryWrapper);
    }

    @PostMapping("")
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
    @GetMapping("/{id}")
    public String getUserById(@PathVariable int id) {
        System.out.println(id);
        return "根据ID获取用户信息";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        int i = userMapper.deleteById(id);
        System.out.println(i);
        if (i > 0) {
            return "删除成功";
        } else {
            return "删除失败";
        }
    }

    @PutMapping("/{id}")
    public String update(@PathVariable int id, @RequestBody User user) {
        // user.setId(id); // 确保 ID 被设置进去
        int result = userMapper.updateById(user);
        return result > 0 ? "更新成功" : "更新失败";
    }

    /**
     * 分页查询用户
     * 
     * @param page     页码，默认第1页
     * @param pageSize 每页大小，默认10条
     * @return 分页结果
     */
    @GetMapping("/page")
    public IPage<User> page(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<User> pageParam = new Page<>(page, pageSize);

        IPage<User> userPage = userMapper.selectPage(pageParam, null);

        return userPage;
    }
}
