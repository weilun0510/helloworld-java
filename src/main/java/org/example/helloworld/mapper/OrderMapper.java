package org.example.helloworld.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.helloworld.entity.Order;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {
    /**
     * 根据用户ID查询该用户的所有订单
     * 
     * @param uid 用户ID
     * @return 订单列表
     */
    @Select("select * from `order` where uid = #{uid}")
    List<Order> selectByUid(@Param("uid") Integer uid);
}
