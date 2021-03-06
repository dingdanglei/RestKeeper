package com.restkeeper.response.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 前端对象转换封装
 * @author dl
 * @param <T>
 */
@Data
public class PageVO<T> {
    /** 总个数*/
    private long counts;
    /** 每页个数*/
    private long pagesize;

    private long pages; //总页数

    private  long page; //当前页

    private List<T> items; //数据记录

    public PageVO(IPage page) {
        this.pagesize = page.getSize();
        this.counts = page.getTotal();
        this.page = page.getCurrent();
        this.pages = page.getPages();
        this.items = page.getRecords();
    }
}
