package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")//重命名
@Slf4j
@Api(tags = "用户端查询店铺接口")
@RequestMapping("/user/shop")

public class ShopController {
    private final RedisTemplate<String, Object> redisTemplate;

    public ShopController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 查询当前店铺的状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取当前店铺的状态")
    public Result<Integer> getStatus(){

        Integer status =(Integer)redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("<获取到店铺的状态为>:{}",status == 1?"<营业中>":"<打烊中>");
        //TODO 返回真实的店铺状态
        return Result.success(status);
    }
}
