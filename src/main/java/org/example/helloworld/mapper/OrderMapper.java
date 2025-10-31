package org.example.helloworld.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.example.helloworld.entity.OrderEntity;
import org.example.helloworld.entity.UserEntity;

import java.util.List;

/**
 * 订单 Mapper 接口
 */
public interface OrderMapper extends BaseMapper<OrderEntity> {
    /**
     * 根据用户ID查询该用户的所有订单
     * 
     * @param uid 用户ID
     * @return 订单列表
     */
    @Select("select * from `order` where uid = #{uid}")
    List<OrderEntity> selectByUid(@Param("uid") Integer uid);

    /**
     * 查询所有订单及其关联的用户
     * 
     * @return 订单列表，每个订单包含其关联的用户
     */
    @Select("select * from `order`")
    @Results(id = "OrderWithUserMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "order_time", property = "orderTime"),
            @Result(column = "total", property = "total"),
            @Result(column = "uid", property = "user", javaType = UserEntity.class, one = @One(select = "org.example.helloworld.mapper.UserMapper.selectById"))
    })
    List<OrderEntity> selectAllOrdersAndUsers();
}
