package org.example.helloworld.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.helloworld.entity.User;

import java.util.List;


public interface UserMapper {
    @Select("select * from user")
    public List<User> find();

    // Insert 方法的返回值表示插入了几条记录
    @Insert("insert into user values (#{username}, #{password})")
    public int insert(User user);
}
