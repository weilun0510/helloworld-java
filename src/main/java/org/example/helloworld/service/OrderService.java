package org.example.helloworld.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.helloworld.entity.OrderEntity;

import java.util.List;

/**
 * 订单服务接口
 * 继承 IService 获得 MyBatis-Plus 提供的 CRUD 方法
 */
public interface OrderService extends IService<OrderEntity> {
    
    /**
     * 查询所有订单及其关联的用户
     * 
     * @return 订单列表
     */
    List<OrderEntity> getAllOrdersWithUsers();
    
    /**
     * 根据用户ID查询订单
     * 
     * @param uid 用户ID
     * @return 订单列表
     */
    List<OrderEntity> getOrdersByUserId(Integer uid);
}

