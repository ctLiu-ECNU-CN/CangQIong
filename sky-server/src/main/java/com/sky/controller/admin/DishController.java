package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 设置菜品状态
     * status:1 起售
     * status:0 停售
     * @return
     */
    @PostMapping("status/{status}")
    @ApiOperation("设置菜品状态")
    public Result dishSetStatus(@PathVariable Integer status, @RequestParam(name = "id") Long id) {
        log.info("<设置菜品状态为>{}", status == 1?"<起售>":"<停售>");
        dishService.setStatus(status, id);
        return Result.success();
    }
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("<新增菜品>");
        dishService.saveWithFlavor(dishDTO);
        //清理新增菜品 所属的 category 列表的缓存
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("<菜品分页查询>");
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 批量删除菜品接口
     */
    @ApiOperation(value = "批量删除菜品")
    @DeleteMapping()
    public Result delete(@RequestParam("ids") List<Long> ids){
        log.info("<批量删除菜品> ids:{}",ids);
        dishService.deleteBatch(ids);
        cleanCache("dish_*");
        return Result.success();
    }


    /**
     * 根据 id 查询菜品
     */
    @ApiOperation(value = "查询菜品操作")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("<根据id 查询菜品> id:{}",id);
        DishVO dishVO= dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品(不是只传 id,前端传来一个 DISHVO 实体
     */
    @ApiOperation(value = "修改菜品操作")
    @PutMapping()
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("<修改菜品>  dish:{}",dishDTO);
//        操作两张表
        dishService.update(dishDTO);
//
        cleanCache("dish_*");
        return Result.success();
    }

    @ApiOperation(value ="根据 categoryid 查询菜品分类")
    @GetMapping("/list/{categoryId}")
    public Result<List<DishVO>> list(@PathVariable Long categoryId){
        log.info("<根据分类 id 查询菜品>  categoryId:{}",categoryId);
        List<DishVO> dishVOList = dishService.getByCategoryId(categoryId);
        return Result.success(dishVOList);
    }

    /**
     * 按照模式匹配的串,清理相关缓存
     * @param pattern String, 需要清理的数据的键值
     */
    private void cleanCache(String pattern){
        log.info("<清理缓存> pattern:{}",pattern);
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }


}
