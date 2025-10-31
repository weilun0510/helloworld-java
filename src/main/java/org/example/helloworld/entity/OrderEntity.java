package org.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 订单实体类
 */
@Data
@TableName("`order`")  // 使用反引号，因为 order 是 MySQL 关键字
public class OrderEntity {
    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /** 订单时间 - 数据库字段名为 order_time */
    @TableField("order_time")
    private Date orderTime;
    
    /** 订单总额 */
    private Integer total;
    
    /** 用户ID - 外键 */
    private Integer uid;

    /** 关联的用户 */
    @TableField(exist = false) // 告诉mybatis-plus,数据库不存在该字段
    private UserEntity user;
}

