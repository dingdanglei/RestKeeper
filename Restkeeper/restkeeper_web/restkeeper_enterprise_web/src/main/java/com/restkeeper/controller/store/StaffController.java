package com.restkeeper.controller.store;

import com.restkeeper.store.entity.Staff;
import com.restkeeper.store.service.IStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dinglei
 * @date 2022/5/4 16:31
 */
@Slf4j
@Api(tags = {"员工管理"})
@RestController
@RequestMapping("/staff")
public class StaffController {

    @Reference(version = "1.0.0" , check = false)
    private IStaffService staffService ;

    @ApiOperation("新增员工")
    @PostMapping("/add")
    public boolean addStaff(@RequestBody Staff staff){
        return staffService.addStaff(staff);
    }

}