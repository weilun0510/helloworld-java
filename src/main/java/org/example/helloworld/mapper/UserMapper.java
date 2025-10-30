package org.example.helloworld.mapper;

import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.helloworld.entity.User;

import java.util.List;

// 使用mybatisplus BaseMapper 提供的方法
public interface UserMapper extends BaseMapper<User> {
}

// public interface UserMapper {
// @Select("select * from user")
// public List<User> selectList();

// // Insert 方法的返回值表示插入了几条记录
// @Insert("insert into user(username, password) values (#{username},
// #{password})")
// @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
// public int insert(User user);

// /**
// * 查询用户及其所有订单（一对多关联查询）
// * 使用 @Results 定义结果映射，通过 @Many 注解实现懒加载关联订单
// *
// * @return 用户列表，每个用户包含其所有订单
// */
// @Select("select * from user")
// @Results(id = "UserWithOrdersMap", value = {
// @Result(column = "id", property = "id", id = true),
// @Result(column = "username", property = "username"),
// @Result(column = "password", property = "password"),
// @Result(column = "id", property = "orders", javaType = List.class, many =
// @Many(select = "org.example.helloworld.mapper.OrderMapper.selectByUid"))
// })
// public List<User> selectAllUserAndOrders();

// /**
// * 根据用户ID查询用户及其所有订单
// *
// * @param id 用户ID
// * @return 用户及其所有订单
// */
// @Select("select * from user where id = #{id}")
// public User selectById(@Param("id") Integer id);

// /**
// * mybatisplus 的条件构造器查询
// *
// */
// public List<User> selectByUsername(QueryWrapper<User> queryWrapper);
// }
