package com.yfan.enums;

import com.yfan.console.Console;

/**
 * @Author: YFAN
 * @CreateTime: 2022-08-07 11:20
 */
public enum StarterType {

    PROJECT(1, "项目启动器"),
    GENERATOR(2, "代码生成器");

    private Integer code;
    private String des;

    private StarterType(Integer code, String des) {
        this.code = code;
        this.des = des;
    }

    public static StarterType getType(String type) {
        Integer num = null;
        try {
            num = Integer.parseInt(type);
        } catch (Exception e) {
            Console.info(e);
        }
        return getType(num);
    }

    private static StarterType getType(Integer code) {
        StarterType type = null;
        for (StarterType value : StarterType.values()) {
            if (value.code.equals(code)) {
                type = value;
            }
        }
        if (type == null) {
            Console.info(new RuntimeException("找不到对应的启动器！code:" + code));
        }
        return type;
    }

    public Integer getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }
}
