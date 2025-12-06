package org.example.helloworld.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.helloworld.entity.OrderEntity;
import org.example.helloworld.service.OrderService;
import org.example.helloworld.utils.BusinessCode;
import org.example.helloworld.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制器
 */
@Tag(name = "订单管理", description = "订单查询相关接口")
@RestController
@RequestMapping("/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  /**
   * 查询所有订单及其关联的用户
   * 
   * @return 订单列表
   */
  @Operation(summary = "查询所有订单", description = "查询所有订单及其关联的用户信息")
  @GetMapping("/findAll")
  public Result findAll() {
    List<OrderEntity> orders = orderService.getAllOrdersWithUsers();
    return Result.ok().data("orders", orders);
  }

  /**
   * 根据用户ID查询订单
   * 
   * @param uid 用户ID
   * @return 订单列表
   */
  @GetMapping("/user/{uid}")
  public Result getOrdersByUserId(@PathVariable Integer uid) {
    List<OrderEntity> orders = orderService.getOrdersByUserId(uid);
    return Result.ok().data("orders", orders);
  }

  /**
   * 根据订单ID查询订单详情
   * 
   * @param id 订单ID
   * @return 订单详情
   */
  @GetMapping("/{id}")
  public Result getById(@PathVariable Integer id) {
    OrderEntity order = orderService.getById(id);
    if (order != null) {
      return Result.ok().data("order", order);
    } else {
      return Result.fail(BusinessCode.ORDER_NOT_FOUND);
    }
  }
}
