package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品同时插入菜品的口味数据
    public void saveWithFlavor(DishDto dishDto);
}
