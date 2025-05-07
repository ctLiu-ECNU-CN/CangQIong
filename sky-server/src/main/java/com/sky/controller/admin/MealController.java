package com.sky.controller.admin;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
public class MealController {
    @Autowired
    SetMealService setMealService;

    @PostMapping()
    @ApiOperation(value = "新增套餐")
    public Result setMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("<新增套餐>");
        setMealService.updateMeal(setmealDTO);
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
        PageResult pageResult = setMealService.list(dishPageQueryDTO);
        return Result.success(pageResult);
    }
}
