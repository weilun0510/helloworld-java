package org.example.helloworld.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.helloworld.entity.OrderEntity;
import org.example.helloworld.mapper.OrderMapper;
import org.example.helloworld.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单服务实现类
 * 继承 ServiceImpl 获得 MyBatis-Plus 提供的 CRUD 方法
 * ServiceImpl<Mapper, Entity>
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    /**
     * 查询所有订单及其关联的用户
     * 
     * @return 订单列表
     */
    @Override
    public List<OrderEntity> getAllOrdersWithUsers() {
        return baseMapper.selectAllOrdersAndUsers();
    }

    /**
     * 根据用户ID查询订单
     * 
     * @param uid 用户ID
     * @return 订单列表
     */
    @Override
    public List<OrderEntity> getOrdersByUserId(Integer uid) {
        if (uid == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return baseMapper.selectByUid(uid);
    }
}

