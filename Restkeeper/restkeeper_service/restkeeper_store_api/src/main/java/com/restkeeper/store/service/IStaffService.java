package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Staff;

public interface IStaffService extends IService<Staff> {

    /**
     * 添加员工
     * @param staff 员工信息
     * @return 添加是否成功
     */
    boolean addStaff(Staff staff);
}
