package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMealMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    DishMealMapper dishMealMapper;

    @Autowired
    SetMealMapper setMealMapper;

    @Override
    /**
     * 更新套餐数据
     * 第一步 更新套餐表的一条记录
     * 第二步 更新套餐里菜品的 n 条记录
     */
    public void updateMeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setMealMapper.insert(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        //更新菜品 n 条记录
        dishMealMapper.insertBatch(setmealDishes);
    }
}
