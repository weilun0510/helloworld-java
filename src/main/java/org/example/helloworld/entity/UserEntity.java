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

    /** 头像 URL（非数据库字段，业务字段） */
    @TableField(exist = false)
    private String avatar;

    /** 描述用户的所有订单（非数据库字段） */
    @TableField(exist = false)
    private List<OrderEntity> orders;
}

