package com.yfan.enums;

import cn.hutool.core.lang.Console;

/**
 * 工具类型
 * @Author: YFAN
 * @CreateTime: 2022-08-07 11:20
 */
public enum ToolType {

    BUILDPROJECT("1", "项目构建器"),
    GENERATOR("2", "代码生成器");

    private String code;
    private String des;

    private ToolType(String code, String des) {
        this.code = code;
        this.des = des;
    }

    public static ToolType getType(String code) {
        ToolType type = null;
        for (ToolType value : ToolType.values()) {
            if (value.code.equals(code)) {
                type = value;
            }
        }
        if (type == null) {
            Console.log(new RuntimeException("找不到对应的启动器！code:" + code));
        }
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }
}
