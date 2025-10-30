package org.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

public class Order {
    @TableId(type = IdType.AUTO)
    private int id;
    private Date order_time;
    private int total;
    private int uid;
}
