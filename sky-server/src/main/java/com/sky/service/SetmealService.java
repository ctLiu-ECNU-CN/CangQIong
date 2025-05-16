package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


public interface SetmealService {
    void updateMeal(SetmealDTO setmealDTO);

    PageResult list(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    void deleteBatch(List<Long> ids);

    void startOrStop(Integer status, Long id);

    SetmealVO getById(Long id);
}
