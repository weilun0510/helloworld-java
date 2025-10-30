package org.example.helloworld.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.helloworld.entity.User;

import java.util.List;

// 使用mybatisplus BaseMapper 提供的方法
// public interface UserMapper extends BaseMapper<User> {
// }

public interface UserMapper {
    @Select("select * from user")
    public List<User> selectList();

    // Insert 方法的返回值表示插入了几条记录
    @Insert("insert into user(username, password) values (#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(User user);

    // 查询用户及其所有订单
    @Select("select * from user")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "id", property = "orders", javaType = List.class, many = @Many(select = "org.example.helloworld.mapper.OrderMapper.selectByUid"))
    })
    public List<User> selectAllUserAndOrders();
}
