package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addCart(ShoppingCartDTO shoppingCartDTO) {
//    判断当前商品是否存在在购物车,如果存在,则数量++;如果不存在,保存在购物车表内
        //select
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        log.info("<当前用户>{}", BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list != null && list.size() > 0) {
            // 存在数据,
            //获取购物车数据,对应的菜品/套餐 数量+1
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            //更新数据
            shoppingCartMapper.updateNumberById(cart);
        }
        else{
        //update or insert
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId != null) {
                //是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                //是套餐
                Setmeal meal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(meal.getName());
                shoppingCart.setImage(meal.getImage());
                shoppingCart.setAmount(meal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

        }
        shoppingCartMapper.insert(shoppingCart);

    }


}
