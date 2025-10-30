package org.example.helloworld.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.helloworld.entity.Order;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {
    @Select("select * from `order` where uid = #{uid}")
    List<Order> selectByUid(int uid);
}
