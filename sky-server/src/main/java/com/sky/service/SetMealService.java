package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealService {
    void updateMeal(SetmealDTO setmealDTO);

    PageResult list(DishPageQueryDTO dishPageQueryDTO);
}
