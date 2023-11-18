package com.zhy.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class MenuForm {

    private Long id;

    private String name;

    private Double price;

    private Integer amount;
}
