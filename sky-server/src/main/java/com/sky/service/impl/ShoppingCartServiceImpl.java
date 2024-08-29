package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO){
        //判断当前加入到购物车中的商品是否已经存在了
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId=BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);


        //判断当前菜品或套餐是否在购物车中
        //select count(id) from shopping_cart where user_id=? and dish_id=? and setmeal_id=? and dish_flavor=?
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);

        if(list!=null&&list.size()>0){
            //如果已经存在，就在原来数量基础上加一
            ShoppingCart cartServiceOne=list.get(0);
            cartServiceOne.setNumber(cartServiceOne.getNumber()+1);
            shoppingCartMapper.updateNumberById(cartServiceOne);
        }else{
            //如果不存在，则添加到购物车，数量默认就是一

            //判断本次添加到购物车的是菜品还是套餐
            Long dishId=shoppingCartDTO.getDishId();
            if(dishId!=null){
                //添加到购物车的是菜品
                Dish dish=dishMapper.getById(dishId);

                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());

            }else{
                //添加到购物车的是套餐
                Long setmealId=shoppingCartDTO.getSetmealId();
                shoppingCart.setAmount(setmealMapper.getById(setmealId).getPrice());
                shoppingCart.setName(setmealMapper.getById(setmealId).getName());
                shoppingCart.setImage(setmealMapper.getById(setmealId).getImage());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);


        }
    }

    /**
     * 查看购物车
     */
    public List<ShoppingCart> list(){
        //获取到当前用户id
        Long userId= BaseContext.getCurrentId();
        ShoppingCart shoppingCart=ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        return list;
    }

    public void clean(){
        Long userId= BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
