package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User weChatlogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());
        if(openid==null){
            throw new RuntimeException(MessageConstant.LOGIN_FAILED);
        }
//        判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        if(user == null){
            //如果是新用户
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
//        如果是新用户,自动完成注册(在用户表里添加用户)
            userMapper.insert(user);
        }
//        返回用户对象
        return user;

    }

    /**
     * 调用微信接口服务, 获取微信用户的 openid
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        //        调用微信接口服务,获取当前微信用户的 openid
        Map<String, String> params = new HashMap<>();
        params.put("appid",weChatProperties.getAppid());
        params.put("secret",weChatProperties.getSecret());
        params.put("js_code",code);
        params.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, params);
//        判断 openid 是否为空,如果为空表示登录失败,抛出业务异常
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
