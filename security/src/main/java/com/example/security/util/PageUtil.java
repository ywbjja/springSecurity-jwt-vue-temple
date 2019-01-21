package com.example.security.util;

import com.github.pagehelper.Page;
import lombok.Data;

import java.util.List;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/21
 * @Description：
 */
@Data
public class PageUtil {
    private Integer pageCur;
    private Integer pageSize;
    private Integer rowTotal;
    private Integer pageTotal;
    private List data;

    public PageUtil(Page page,List data) {
        this.pageCur = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.rowTotal = page.getPages();
        this.pageTotal = Integer.valueOf((int)page.getTotal());
        this.data = data;
    }
}
