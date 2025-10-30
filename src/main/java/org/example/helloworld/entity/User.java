package org.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("user")
public class User {
    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;

    // 描述用户的所有订单
    @TableField(exist = false) // 告诉mybatis-plus,数据库不存在该字段
    private List<Order> orders;

    // public int getId() {
    // return id;
    // }
    //
    // public void setId(int id) {
    // this.id = id;
    // }
    //
    // public String getUsername() {
    // return username;
    // }
    //
    // public void setUsername(String username) {
    // this.username = username;
    // }
    //
    // public String getPassword() {
    // return password;
    // }
    //
    // public void setPassword(String password) {
    // this.password = password;
    // }
}
