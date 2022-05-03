package com.restkeeper.shop;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.service.IStoreService;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author dinglei
 * @date 2022/5/3 14:33
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class StoreTest extends BaseTest{

    @Reference(version = "1.0.0",check = false)
    private IStoreService storeService;


    @Test
    @Rollback(false)
    public void saveTest(){
        Store store = new Store();
        store.setBrandId("test1");
        store.setStoreName("测试1");
        store.setProvince("北京1");
        store.setCity("昌平区1");
        store.setArea("金燕龙大厦1");
        store.setAddress("北京 昌平区 金燕龙大厦1");
        storeService.save(store);
    }


    @Test
    public void queryTest(){
        Store store = storeService.getById("1521378011092377602");
        System.out.println(store);
    }


    @Test
    public void queryPage(){
        IPage<Store> result = storeService.queryPageByName(1, 10, "门");

        List<Store> storeList = result.getRecords();

        System.out.println(storeList);
    }

    @Test
    @Rollback(false)
    public void disableStore(){
        Store store = storeService.getById("1209067455826198529");
        store.setStatus(SystemCode.FORBIDDEN);
        storeService.updateById(store);
    }

}
