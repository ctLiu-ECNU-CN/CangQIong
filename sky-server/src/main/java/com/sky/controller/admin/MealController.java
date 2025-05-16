package com.sky.controller.admin;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
public class MealController {
    @Autowired
    SetmealService setmealService;

    @PostMapping()
    @ApiOperation(value = "新增套餐")
//    精确清理 redis 缓存
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result setMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("<新增套餐>");
        setmealService.updateMeal(setmealDTO);
        return Result.success();
    }

    @PutMapping()
    @ApiOperation(value = "修改套餐")
    public Result updateMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("<修改套餐>");
//        setMealService.updateMeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询套餐")
    public Result<PageResult> list(@RequestParam DishPageQueryDTO dishPageQueryDTO) {
        log.info("<分页查询套餐>");
        PageResult pageResult = setmealService.list(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除")
    @CacheEvict(cacheNames = "setmealCache",key = )
    public Result delete(@RequestParam List<Long> ids){
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}
