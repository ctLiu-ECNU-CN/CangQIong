package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishMealMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Autowired
    DishMealMapper dishMealMapper;

    @Override
    /**
     * 新增菜品和口味
     */
    @Transactional(rollbackFor = Exception.class) //保证事务一致性(因为要操作两张表
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        //copy dishDto ---> dish
        BeanUtils.copyProperties(dishDTO, dish);
        //菜品表插入一条数据
        dishMapper.insert(dish);
        //口味表插入 n 条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 新增套餐中的菜品
     * @param dishMeal
     */
    @Override
    public void saveMealDish(List<SetmealDish> dishMeal) {
        dishMealMapper.insertBatch(dishMeal);
    }
}
