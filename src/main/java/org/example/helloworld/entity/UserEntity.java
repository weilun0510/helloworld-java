package org.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class UserEntity {
    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /** 用户名 */
    private String username;
    
    /** 密码 */
    private String password;

    /** 描述用户的所有订单 */
    @TableField(exist = false) // 告诉mybatis-plus,数据库不存在该字段
    private List<OrderEntity> orders;
}

