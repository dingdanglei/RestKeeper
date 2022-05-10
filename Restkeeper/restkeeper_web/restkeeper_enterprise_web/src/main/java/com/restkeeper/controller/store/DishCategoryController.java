package com.restkeeper.controller.store;

import com.restkeeper.response.vo.PageVO;
import com.restkeeper.store.entity.DishCategory;
import com.restkeeper.store.service.IDishCategoryService;
import com.restkeeper.vo.store.AddDishCategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = {"分类管理"})
@RestController
@RequestMapping("/dishCategory")
public class DishCategoryController {

    @Reference(version = "1.0.0." , check = false )
    private IDishCategoryService dishCategoryService ;

    @ApiOperation("添加分类")
    @PostMapping("/add")
    public boolean add(@RequestBody AddDishCategoryVO addDishCategoryVO){
        return dishCategoryService.add(addDishCategoryVO.getCategoryName(),addDishCategoryVO.getType());
    }

    @ApiOperation("修改分类")
    @PutMapping("/update/{id}")
    public boolean update(@PathVariable("id") String id , @RequestParam("categoryName") String categoryName ){
        return dishCategoryService.update(id, categoryName);
    }

    @ApiOperation("分类分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "分页大小", required = true, dataType = "Integer")})
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO<DishCategory> findByPage(@PathVariable("page") int page , @PathVariable("pageSize") int pageSize){
        return new PageVO<DishCategory>(dishCategoryService.queryPage(page , pageSize));
    }

    @ApiOperation("分类列表查询")
    @GetMapping("/type/{type}")
    public List<Map<String,Object>> getByType(@PathVariable("type") Integer type){
        return dishCategoryService.findCategoryList(type);
    }


}
