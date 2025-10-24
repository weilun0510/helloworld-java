package org.example.helloworld.controller;

import org.example.helloworld.entity.User;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(String name, String phone){
        System.out.println(phone);
        return "Hello " + name;
    }

    @GetMapping("/getName")
    public String getName(@RequestParam("nickname") String name ){
        // 参数映射 nickname => name。添加映射时该参数默认必传
        // 如果前端未传，会报异常错误
        System.out.println("nickname:"+name);
        return "Hello " + name;
    }

    @GetMapping("/getName1")
    public String getName1(@RequestParam(value = "nickname", required = false) String name ){
        // 参数映射 nickname => name。添加映射也可以改为不传
        System.out.println("nickname:"+name);
        return "Hello " + name;
    }

    // post请求(普通表单接口 用于接收 x-www-form-urlencoded 或 form-data 格式)
    @PostMapping("/postTest1")
    public String postTest1(String name ){
        System.out.println("name:"+name);
        return "postTest1 " + name;
    }

    // post请求，多参数
    // 接收json格式需要添加 @RequestBody 注解
    @PostMapping("/postTest2")
//    @RequestMapping(value = "/postTest2", method = RequestMethod.POST)
    public String postTest2(@RequestBody User user){
        // 如果打印内容没显示字段值，可以使用Lombok @Data简化
        System.out.println(user);
        return "post请求 json格式";
    }

//    @GetMapping("/test/*") *一层 **多层
    @GetMapping("/test/**")
    public String test(){
        return "通配符请求";
    }
}
