package org.example.helloworld.controller;

import java.util.List;

import org.example.helloworld.entity.OrderEntity;
import org.example.helloworld.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {
  @Autowired
  private OrderMapper orderMapper;

  /**
   * 查询所有订单及其关联的用户
   * 
   * @return 订单列表
   */
  @GetMapping("/findAll")
  public List<OrderEntity> findAll() {
    return orderMapper.selectAllOrdersAndUsers();
  }
}
