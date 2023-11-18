package com.zhy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhy.common.vo.Result;
import com.zhy.entity.Menu;
import com.zhy.entity.User;
import com.zhy.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private IMenuService iMenuService;

    @GetMapping("/all")
    public Result<List<Menu>> getAll() {
        List<Menu> list = iMenuService.list();
        System.out.println("----Menu--all--success----");
        return Result.success(list, "查询成功");
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getAllByPage(@RequestParam(value = "foodName",required = false) String foodName,
                                                    @RequestParam(value = "foodType",required = false) String foodType,
                                                    @RequestParam(value = "pageNo") Long pageNo,
                                                    @RequestParam(value = "pageSize") Long pageSize,
                                                    @RequestParam(value = "state") int state) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(foodName), Menu::getName, foodName);
        wrapper.like(StringUtils.hasLength(foodType), Menu::getType, foodType);
        System.out.println(state);
        wrapper.eq(Menu::getState, state);
        wrapper.orderByAsc(Menu::getId);

        Page<Menu> page = new Page<>(pageNo,pageSize);
        iMenuService.page(page, wrapper);
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());
        System.out.println("foodName: " + foodName);
        System.out.println("foodType: " + foodType);
        System.out.println("----Menu--list--success----");
        return Result.success(data);
    }

    @GetMapping("/foodlist")
    public Result<Map<String, Object>> getAllByCondition(@RequestParam(value = "foodName",required = false) String foodName,
                                                         @RequestParam(value = "foodType",required = false) String foodType,
                                                         @RequestParam(value = "pageNo") Long pageNo,
                                                         @RequestParam(value = "pageSize") Long pageSize) {

        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(foodName), Menu::getName, foodName);
        wrapper.like(StringUtils.hasLength(foodType), Menu::getType, foodType);
        wrapper.orderByAsc(Menu::getId);

        Page<Menu> page = new Page<>(pageNo, pageSize);
        iMenuService.page(page, wrapper);

        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());

        return Result.success(data);
    }

    @GetMapping("/selectById")
    public Result<Menu> selectById(@RequestParam(value = "foodId") String foodId) {
        Menu menu = iMenuService.getById(foodId);
        //防止修改时 不添加图片导致null
        imageName = menu.getPicture().substring(menu.getPicture().lastIndexOf("/") + 1);
        System.out.println("imageName: " + imageName);
        return Result.success(menu);
    }

    private final String virtualUrl = "http://localhost:8080/picture/";
    private String imageName = "";
    private String imageUrl = "";

    @PostMapping
    public Result<?> addFood(@RequestBody Menu menu) {
        menu.setPicture(virtualUrl + imageName);
        if(iMenuService.getById(menu.getId()) != null) {
            iMenuService.updateById(menu);
            System.out.println("----menu--update--success----");
            return Result.success("修改菜品成功");
        } else {
            iMenuService.save(menu);
            System.out.println("----menu--add--success----");
            return Result.success("新增菜品成功");
        }
    }

    @PostMapping("/saveImage")
    public Result<?> saveImage(@RequestBody MultipartFile file) {
        System.out.println("----menu--saveImage----");
        imageName = file.getOriginalFilename();
        File imageFile = new File("E:\\workspace\\Restaurant-ordering-management-system\\picture", imageName);
        imageUrl = imageFile.getAbsolutePath();
        try {
            file.transferTo(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("imageName:" + imageName);
        System.out.println("imageUrl:" + imageUrl);
        return Result.success("成功保存图片");
    }

    @DeleteMapping
    public Result<?> deleteById(@RequestParam(value = "foodId") String foodId) {
        if(iMenuService.getById(foodId) != null) {
            iMenuService.removeById(foodId);
            return Result.success("成功删除菜品");
        }
        return Result.fail("菜品id不存在");
    }
}
