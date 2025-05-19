package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;
    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //如果地址簿为空,抛出异常
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //查询购物车
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartListlist = shoppingCartMapper.list(shoppingCart);

        // 如果购物车为空,抛出异常
        if(shoppingCartListlist == null || shoppingCartListlist.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 订单表里插入一条数据
        Orders orders = new Orders();
//        orders.setId(BaseContext.getCurrentId());//id 是自增主键,不用设置
        BeanUtils.copyProperties(ordersSubmitDTO, orders);//属性拷贝
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);//未付款
        orders.setStatus(Orders.PENDING_PAYMENT);//等待付款
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setUserId(BaseContext.getCurrentId());
        orders.setConsignee(addressBook.getConsignee());
        orderMapper.insert(orders);


        //订单明细表里插入 N 条数据
        List<OrderDetail> orderDetails= new ArrayList<>();
        for (ShoppingCart cart: shoppingCartListlist) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        if(orderDetails.size() > 0){
            orderDetailMapper.insertBatch(orderDetails);

        }

        //清空用户购物车数据
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
        // 返回结果
//        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber()).build();
        return orderSubmitVO;
    }
}
