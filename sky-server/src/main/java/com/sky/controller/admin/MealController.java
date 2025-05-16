package com.sky.controller.admin;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
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
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//清除所有缓存
    public Result updateMeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("<修改套餐>");
//        setMealService.updateMeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询套餐")
    public Result<PageResult> list(@RequestParam Integer page, @RequestParam Integer pageSize,Integer status) {
        log.info("<分页查询套餐>");
        DishPageQueryDTO dishPageQueryDTO = new DishPageQueryDTO();
        dishPageQueryDTO.setPage(page);
        dishPageQueryDTO.setPageSize(pageSize);
        if (status != null) {
            dishPageQueryDTO.setStatus(status);

        }
        PageResult pageResult = setmealService.list(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//清除所有缓存
    public Result delete(@RequestParam List<Long> ids){
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//清除所有缓存
    public Result startOrStop(@PathVariable Integer status, Long id){
        setmealService.startOrStop(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据 id 查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
        SetmealVO meal = setmealService.getById(id);
        return Result.success(meal);
    }
}
