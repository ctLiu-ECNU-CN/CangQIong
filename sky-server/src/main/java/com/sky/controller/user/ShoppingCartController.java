package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import com.sky.service.impl.ShoppingCartServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "C端购物车接口")
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private ShoppingCartServiceImpl shoppingCartServiceImpl;

    /**
     *  添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @RequestMapping("/add")
    @ApiOperation(value = "添加购物车")

    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("<添加购物车>:{}", shoppingCartDTO);
        shoppingCartServiceImpl.addCart(shoppingCartDTO);
        return Result.success();
    }


    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    @ApiOperation( value = "查看购物车")
    public Result<List<ShoppingCart>> findAll() {
        log.info("<查询购物车>");
        List<ShoppingCart> list = shoppingCartService.list();
        //返回购物车结果 list
        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation(value = "清空购物车")
    public Result clean() {
        log.info("<清空购物车>");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }
}
