package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealService {
    void updateMeal(SetmealDTO setmealDTO);
}
