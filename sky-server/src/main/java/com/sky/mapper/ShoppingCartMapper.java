package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 根据 id 查询购物车内的所有物品
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据 id 更新数量
     * @param cart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")//fix 不能根据用户 id 查询,不然该用户所有的商品数量都会 更新
    void updateNumberById(ShoppingCart cart);

    /**
     * 购物车表插入记录
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name,user_id,dish_id,setmeal_id,image,dish_flavor,number,amount,create_time) values (#{name},#{userId},#{dishId},#{setmealId},#{image},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);
}
