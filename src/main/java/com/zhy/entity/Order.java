package com.zhy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhy
 * @since 2023-08-29
 */
@TableName("tb_order")
@Getter
@Setter
@ToString
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    // 不能将列名设为table  与sql语句关键字冲突
    @TableField(value = "table_num")
    private String tableNum;

    private Integer amount;

    private Double money;

    private Date time;

    private String remark;

    public Order() {

    }

    public Order(String name, Integer amount) {
        this.name = name;
        this.amount = amount;
    }

}
