package com.zhy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhy.common.vo.Result;
import com.zhy.entity.Menu;
import com.zhy.entity.MenuForm;
import com.zhy.entity.Order;
import com.zhy.service.IMenuService;
import com.zhy.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhy
 * @since 2023-08-29
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IMenuService iMenuService;
    @Autowired
    private IOrderService iOrderService;

    private String tableNum = "", remark = "";
    private List<Order> orderList = new ArrayList<>();

    @PostMapping
    public Result<?> addFood(@RequestBody List<MenuForm> tableSubmit) {
//                             @RequestParam(value = "tableNum") String tableNum,
//                             @RequestParam(value = "remark") String remark) {
//        List<Order> list = new ArrayList<>();
        System.out.println(tableSubmit);
        orderList = new ArrayList<>();
        for(MenuForm form : tableSubmit) {
            if(form != null && form.getId() != null && form.getAmount() > 0) {
                Order order = new Order();
//                order.setTable(Integer.parseInt(tableNum));
                order.setName(form.getName());
                order.setAmount(form.getAmount());
                order.setMoney(form.getPrice() * form.getAmount());
                order.setTime(new Date());
//                order.setRemark(remark);
                orderList.add(order);
//                iOrderService.save(order);
            }
        }
        return Result.success("订单生成成功");
    }


    @PostMapping("/info")
    public Result<?> getTableInfo(@RequestParam(value = "tableNum") String tableNum,
                                @RequestParam(value = "remark") String remark) {
        System.out.println("orderList:" + orderList);
        System.out.println("tableNum:" + tableNum);
        System.out.println("remark:" + remark);
        for(Order order : orderList) {
            order.setTableNum(tableNum);
            order.setRemark(Objects.requireNonNullElse(remark, "null"));
            System.out.println(order);
            iOrderService.save(order);
        }
        return Result.success("信息获取成功");
    }

    @GetMapping("/all")
    public Result<List<Order>> getAll() {
        List<Order> list = iOrderService.list();
        System.out.println("----Order--all--success----");
        return Result.success(list, "查询成功");
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getAllByPage(@RequestParam(value = "pageNo") Long pageNo,
                                                    @RequestParam(value = "pageSize") Long pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getId);

        Page<Order> page = new Page<>(pageNo,pageSize);
        iOrderService.page(page, wrapper);
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());
        System.out.println("----Order--list--success----");
        return Result.success(data);
    }
}
