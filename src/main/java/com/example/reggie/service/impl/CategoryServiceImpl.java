package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前判断是否关联菜品和套餐
     * @param id:
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();

        //查询当前分类是否关联菜品
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishQueryWrapper);
        if(count1>0){
            //关联了菜品，抛出一个业务异常
            throw new CustomException("删除失败：当前分类关联了菜品");

        }
        //查询当前分类是否关联套餐
        LambdaQueryWrapper<Setmeal> SetmealQueryWrapper = new LambdaQueryWrapper<>();
        SetmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(SetmealQueryWrapper);
        if(count2>0){
            //关联了套餐，抛出一个业务异常
            throw new CustomException("删除失败：当前分类关联了套餐");
        }

        //正常删除
        super.removeById(id);

    }
}
