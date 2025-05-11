package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishMealMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private SetmealMapper setMealMapper;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        int page = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);
        Page<DishVO> dish = dishMapper.page(dishPageQueryDTO);
        //TODO dish count
        Integer count = dishMapper.count(dishPageQueryDTO);
        return new PageResult(dish.getPages(),dish);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        /**
         * 删除需要判断是否能够删除,检查以下条件
         * 1.是否存在起售中的菜品
         * 2.是否属于某个套餐
         */
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品起售中,不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

        }

        List<Long> setmealIdsByDishIds = setMealMapper.getSetmealIdsByDishIds(ids);
        if(setmealIdsByDishIds != null && setmealIdsByDishIds.size() > 0) {
            //说明存在菜品在套餐中,不允许删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        /**
         * 可以删除的情况下,还需要做:
         * 1.删除菜品表中的菜品数据
         * 2.删除菜品关联的口味数据
         */

        // TODO for 循环多次执行 SQL 语句优化成单条 SQL 语句
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            dishFlavorMapper.deleteById(id);
//        }
        dishMapper.deleteById(ids);
        dishFlavorMapper.deleteById(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO = new DishVO();
        //查询菜品表
        Dish dish = dishMapper.getById(id);
        //查询口味表
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 根据 id 修改菜品表
     * 需要操作 菜品表和口味表
     * 对于口味表,先删除,再插入
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
//        修改菜品表基本信息
        Dish dish = new Dish();
//        dishDTO ---> copy dish
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
//        删除原有口味数据,重新插入口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        dishDTO.getFlavors().forEach(flavor -> {
            flavor.setDishId(dish.getId());
        });
        dishFlavorMapper.insertBatch(dishDTO.getFlavors());

    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}