package org.example.helloworld.controller;

import java.util.List;

import org.example.helloworld.entity.Order;
import org.example.helloworld.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
  @Autowired
  private OrderMapper orderMapper;

  @GetMapping("/order/findAll")
  public List<Order> findAll() {
    return orderMapper.selectAllOrdersAndUsers();
  }
}
