package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    //@AutoFill(value = OperationType.INSERT)
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 删除指定 id 的菜品口味
     * @param dishIds
     */
    void deleteById(List<Long> dishIds);

    /**
     * 根据菜品 id 查询所有的菜品口味
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    /**
     * 根据菜品 Id 删除所有口味
     * @param id
     */
    @Delete("delete from dish_flavor where dish_id = #{id} ")
    void deleteByDishId(Long id);
}
