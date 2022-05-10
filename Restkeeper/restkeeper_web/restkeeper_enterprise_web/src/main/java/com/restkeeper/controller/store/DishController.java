package com.restkeeper.controller.store;

import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.DishFlavor;
import com.restkeeper.store.service.IDishService;
import com.restkeeper.vo.store.DishFlavorVO;
import com.restkeeper.vo.store.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author dinglei
 * @date 2022/5/4 20:24
 */
@Slf4j
@Api(tags = {"菜品管理"})
@RestController
@RequestMapping("/dish")
public class DishController {
    @Reference(version = "1.0.0" , check = false)
    private IDishService dishService ;

    @ApiOperation("添加菜品")
    @PostMapping("/add")
    public boolean add(@RequestBody DishVO dishVO){
        // 设置菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishVO,dish);
        //设置口味
        List<DishFlavorVO> dishFlavors = dishVO.getDishFlavors();
        List<DishFlavor> dishFlavorList =  new ArrayList<>();
        for(DishFlavorVO dishFlavorVO : dishFlavors){
            DishFlavor dishFlavor = new DishFlavor();
            dishFlavor.setFlavorName(dishFlavorVO.getFlavor());
            dishFlavor.setFlavorValue(dishFlavorVO.getFlavorData().toString());
            dishFlavorList.add(dishFlavor);
        }
        return dishService.save(dish,dishFlavorList);
    }

    @ApiOperation("根据id获取菜品信息")
    @GetMapping("/{id}")
    public DishVO getDish(@PathVariable("id") String id){
        Dish dish = dishService.getById(id);
        if(dish==null){
            throw new BussinessException("菜品不存在");
        }
        DishVO dishVo =new DishVO();
        BeanUtils.copyProperties(dish,dishVo);
        //口味列表
        List<DishFlavorVO> dishFlavorVOList=new ArrayList<>();
        List<DishFlavor> dishFlavorList = dish.getFlavorList();
        for(DishFlavor dishFlavor : dishFlavorList){
            DishFlavorVO dishFlavorVO= new DishFlavorVO();
            dishFlavorVO.setFlavor(dishFlavor.getFlavorName());
            String flavorValue = dishFlavor.getFlavorValue();
            //处理字符串数组
            String quFlavorValue=flavorValue.substring(flavorValue.indexOf("[")+1,flavorValue.indexOf("]"));
            if(StringUtils.isNotEmpty(quFlavorValue)){
                String[] flavor_array= quFlavorValue.split(",");
                dishFlavorVO.setFlavorData(Arrays.asList(flavor_array));
            }
            dishFlavorVOList.add(dishFlavorVO);
        }
        dishVo.setDishFlavors(dishFlavorVOList);
        return  dishVo;
    }


    @ApiOperation("修改菜品")
    @PutMapping("/update")
    public boolean update(@RequestBody DishVO dishVO){
        Dish dish = dishService.getById(dishVO.getId());
        BeanUtils.copyProperties(dishVO,dish);
        //设置口味
        List<DishFlavor> flavorList = setDishFlavors(dishVO);
        return dishService.update(dish,flavorList);
    }

    private List<DishFlavor> setDishFlavors(DishVO dishVO) {
        List<DishFlavorVO> dishFlavorsVO = dishVO.getDishFlavors();
        List<DishFlavor> flavorList = new ArrayList<DishFlavor>();
        for (DishFlavorVO dishFlavorVO : dishFlavorsVO) {
            DishFlavor dishFlavor = new DishFlavor();
            dishFlavor.setFlavorName(dishFlavorVO.getFlavor());
            dishFlavor.setFlavorValue(dishFlavorVO.getFlavorData().toString());
            flavorList.add(dishFlavor);
        }
        return flavorList;
    }

    @ApiOperation("根据分类和名称查询可用的菜品列表")
    @GetMapping("/findEnableDishList/{categoryId}")
    public List<Map<String,Object>> findEnableDishList(@PathVariable("categoryId") String categoryId ,
                                                       @RequestParam(value="name" , required = false) String name){
        return dishService.findEnableDishListInfo(categoryId,name);
    }
}
