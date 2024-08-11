package com.sky.mapper;

import com.sky.constant.MessageConstant;
import com.sky.exception.DeletionNotAllowedException;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

}

