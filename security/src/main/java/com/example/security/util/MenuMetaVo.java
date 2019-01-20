package com.example.security.util;

import lombok.Data;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/18
 * @Description：
 */
@Data
public class MenuMetaVo {
    private String title;

    private String icon;

    public MenuMetaVo(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }
}
