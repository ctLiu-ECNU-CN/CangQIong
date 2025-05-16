package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMealMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)

public class SetmealServiceImpl implements SetmealService {
    @Autowired
    DishMealMapper dishMealMapper;

    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    private DishService dishService;

    @Override
    /**
     * 更新套餐数据
     * 第一步 更新套餐表的一条记录
     * 第二步 更新套餐里菜品的 n 条记录
     */
    public void updateMeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.insert(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        //更新菜品 n 条记录
        dishMealMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult list(DishPageQueryDTO dishPageQueryDTO) {
        //开启分页
        int pageSize = dishPageQueryDTO.getPageSize();
        int page = dishPageQueryDTO.getPage();
        PageHelper.startPage(page, pageSize);
        List<SetmealVO>  SetMeals = setmealMapper.pageQuery(dishPageQueryDTO);
        Integer total = setmealMapper.count();
        // TODO 查询总记录数
        return new PageResult(total,SetMeals);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        setmealMapper.deleteById(ids);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        setmealMapper.updateStatus(status,id);
    }

    /**
     * 根据 id 查询菜品信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        //查询套餐信息
        Setmeal setmeal = setmealMapper.getById(id);
        //查询套餐里所有菜品的信息
        List<SetmealDish> dishList = setmealMapper.getDishById(setmeal.getId());

        //构建 VO
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(dishList);
        return setmealVO;
    }
}
