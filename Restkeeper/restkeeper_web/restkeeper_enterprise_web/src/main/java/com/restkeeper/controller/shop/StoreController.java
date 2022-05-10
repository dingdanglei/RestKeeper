package com.restkeeper.controller.shop;

import com.restkeeper.constants.SystemCode;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.shop.dto.StoreDTO;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.service.IStoreService;
import com.restkeeper.utils.Result;
import com.restkeeper.vo.shop.AddStoreVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dinglei
 * @date 2022/5/3 17:11
 */
@Slf4j
@Api(tags = { "门店信息" })
@RestController
@RequestMapping("/store")
public class StoreController {
    @Reference(version = "1.0.0", check=false)
    private IStoreService storeService;

    @ApiOperation(value = "分页查询所有门店")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "分页大小", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "门店名称", required = false, dataType = "String") })
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO<Store> findListByPage(@PathVariable("page") int page , @PathVariable("pageSize") int pageSize ,
                                        @RequestParam(value = "name" , required = false) String name){
        return new PageVO<Store>(storeService.queryPageByName(page,pageSize,name ));
    }


    @ApiOperation(value = "新增门店")
    @PostMapping("/add")
    public boolean add(@RequestBody AddStoreVO addStoreVO){
        Store store = new Store();
        BeanUtils.copyProperties(addStoreVO, store);
        return storeService.save(store);
    }

    @ApiOperation(value = "门店停用")
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键", required = true, dataType = "String")
    @PutMapping("/disabled/{id}")
    public boolean disabled(@PathVariable("id") String id){
        Store store = storeService.getById(id);
        store.setStatus(SystemCode.FORBIDDEN);
        return storeService.updateById(store);
    }


    @ApiOperation(value = "获取门店省份信息")
    @GetMapping("/listProvince")
    @ResponseBody
    public List<String> listProvince(){
        return storeService.getAllProvince();
    }

    @ApiOperation(value = "根据省份获取门店列表")
    @ApiImplicitParam(paramType = "path" , name = "province" , value = "省份" , required = true , dataType = "String")
    @GetMapping("/getStoreByProvince/{province}")
    @ResponseBody
    public List<StoreDTO> getStoreByProvince(@PathVariable("province") String province){
        return storeService.getStoreByProvince(province);
    }

    @ApiOperation("获取当前商户管理员的门店信息")
    @GetMapping("/listManagerStores")
    public List<StoreDTO> getStoreListByManagerId(){
        return this.getStoreListByManagerId();
    }

    @ApiOperation("门店切换")
    @ApiImplicitParam(paramType = "path" , name = "storeId" , value = "门店id" , required = true , dataType = "String")
    @GetMapping("/switchStore/{storeId}")
    public Result switchStore(@PathVariable("storeId") String storeId){
        return this.switchStore(storeId);
    }


}
