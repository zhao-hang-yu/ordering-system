package com.zhy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhy.common.vo.Code;
import com.zhy.common.vo.Result;
import com.zhy.entity.Menu;
import com.zhy.entity.User;
import com.zhy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhy
 * @since 2023-08-29
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    private String currentToken;

    @GetMapping("/all")
    public Result<List<User>> getAll() {
        List<User> list = userService.list();
        System.out.println("----user--all--success----");
        return Result.success(list, "查询成功");
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        Map<String,Object> data = userService.login(user);
        if(data != null){
            System.out.println("----user--login--success----");
            return Result.success(data);
        }
        return Result.fail(Code.POST_ERR,"用户名或密码错误");
    }

    @GetMapping("/info")
    public Result<Map<String,Object>> getUserInfo(@RequestParam("token") String token){
        // 根据token获取用户信息，redis
        Map<String,Object> data = userService.getUserInfo(token);
        System.out.println("---user--info--data: " + data);
        if(data != null){
            System.out.println("----user--info--success----");
            currentToken = token;
            return Result.success(data);
        }
        currentToken = null;
        return Result.fail(Code.GET_ERR,"登录信息无效，请重新登录");
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("X-Token") String token){
        userService.logout(token);
        System.out.println("----user--logout--success----");
        return Result.success();
    }

    @GetMapping("/getCurrent")
    public Result<?> getCurrent() {
        System.out.println("----user--getCurrent--ing----");
        String username = null;
        if(currentToken != null) {
            System.out.println("token:" + currentToken);
            username = userService.getUserInfo(currentToken).get("name").toString();
//            System.out.println("username:" + username);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StringUtils.hasLength(username), User::getUsername, username);
            User user = userService.getOne(wrapper);
            System.out.println("user:" + user);
            Map<String,Object> data = new HashMap<>();
            data.put("username",user.getUsername());
            data.put("password",user.getPassword());
            data.put("phone",user.getPhone());
            System.out.println("----user--getCurrent--success----");
            return Result.success(data);
        }
        return Result.fail("登录信息无效");
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody User user) {
        System.out.println("----user--update--ing----");
        String username = null;
        if(currentToken != null) {
            username = userService.getUserInfo(currentToken).get("name").toString();
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StringUtils.hasLength(username), User::getUsername, username);
            User user2 = userService.getOne(wrapper);
            user2.setUsername(user.getUsername());
            user2.setPhone(user.getPhone());
            userService.updateById(user2);
            System.out.println("----user--update--success----");
            logout(currentToken);
            return Result.success("修改成功");
        }
        return Result.fail("登录信息无效");
    }
}
