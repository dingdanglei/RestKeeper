package com.restkeeper.controller.shop;

import com.restkeeper.response.vo.PageVO;
import com.restkeeper.shop.entity.StoreManager;
import com.restkeeper.shop.service.IStoreManagerService;
import com.restkeeper.vo.shop.StoreManagerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

/**
 * @author dinglei
 * @date 2022/5/3 22:17
 */
@RestController
@RequestMapping("/storeManger")
@Api(tags = {"门店管理员接口"})
public class StoreManagerController {

    @Reference(version = "1.0.0",check = false)
    private IStoreManagerService storeManagerService;

    /**
     * 查询分页数据
     */
    @ApiOperation(value = "查询分页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页码", required = false, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "分大小", required = false, dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "criteria", value = "查询条件", required = false, dataType = "String") })
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO<StoreManager> findListByCriteria(@PathVariable("page") int page , @PathVariable("pageSize") int pageSize ,
                                                   @RequestParam(value = "criteria" ,required = false) String criteria){
        return new PageVO<>(storeManagerService.queryPageByCriteria(page,pageSize,criteria));
    }


    @ApiOperation("添加店长")
    @PostMapping("/add")
    public boolean add(@RequestBody StoreManagerVO storeManagerVO){
        return storeManagerService.addStoreManager(storeManagerVO.getName(),storeManagerVO.getPhone(),storeManagerVO.getEmail(),storeManagerVO.getStoreIds());
    }

    @ApiOperation("店长信息修改")
    @PutMapping("/update")
    public boolean update(@RequestBody StoreManagerVO storeManagerVO){
        return storeManagerService.updateStoreManager(storeManagerVO.getId(),storeManagerVO.getName(),
                storeManagerVO.getPhone(),storeManagerVO.getEmail(),storeManagerVO.getStoreIds());
    }

    @ApiOperation("门店管理员停用")
    @PutMapping("/pause/{id}")
    public boolean pause(@PathVariable("id") String id){
        return storeManagerService.pauseStoreManager(id);
    }

    @ApiOperation("删除管理员")
    @DeleteMapping("/del/{id}")
    public boolean delete(@PathVariable("id") String id){
        return storeManagerService.deleteStoreManager(id);
    }
}
