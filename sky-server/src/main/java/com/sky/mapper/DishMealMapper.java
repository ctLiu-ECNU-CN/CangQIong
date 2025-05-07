package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMealMapper {

    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<SetmealDish> dishMeal);
}
