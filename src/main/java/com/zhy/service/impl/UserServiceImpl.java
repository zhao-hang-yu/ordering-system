package com.zhy.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhy.entity.User;
import com.zhy.mapper.UserMapper;
import com.zhy.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhy
 * @since 2023-08-29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private RedisTemplate redisTemplate;

//    @Override
//    public Map<String, Object> login(User user) {
//        // 根据用户名和密码查询
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(User::getUsername,user.getUsername());
//        wrapper.eq(User::getPassword,user.getPassword());
//
//        User loginUser = this.baseMapper.selectOne(wrapper);
//        // 结果不为空，则生成token，并将用户信息存入redis
//        if(loginUser != null){
//            String key = "user:" + UUID.randomUUID();
//
//            // 存入redis
//            loginUser.setPassword(null);
//            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);
//
//            // 返回数据
//            Map<String, Object> data = new HashMap<>();
//            data.put("token",key);
//            return data;
//        }
//        return null;
//    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(User user) {
        System.out.println("password:" + passwordEncoder.encode(user.getPassword()));
        // 根据用户名查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,user.getUsername());
        User loginUser = this.baseMapper.selectOne(wrapper);
        // 结果不为空，则生成token，并将用户信息存入redis
        // 判断密码是否与加密的匹配
        if(loginUser != null && passwordEncoder.matches(user.getPassword(), loginUser.getPassword())){
            // UUID
            String key = "user:" + UUID.randomUUID();
            // 存入redis
            loginUser.setPassword(null);
            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);

            // 返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token",key);
            return data;
        }
        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        // 根据token获取用户信息，redis
        Object obj = redisTemplate.opsForValue().get(token);
        if(obj != null){
            User loginUser = JSON.parseObject(JSON.toJSONString(obj),User.class);
            Map<String, Object> data = new HashMap<>();
            data.put("name",loginUser.getUsername());

            // 角色
            data.put("status",loginUser.getRole());

            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }
}