package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 插入订单数据
 */
@Mapper
public interface OrderMapper {
    void insert(Orders ordersSubmitDTO);
}
