package com.yfan.console;

import cn.hutool.log.dialect.console.ConsoleLog;

/**
 * @Author: YFAN
 * @CreateTime: 2022-08-06 23:36
 */
public class Console {

    private static ConsoleLog console = new ConsoleLog("Main");

    public static void info(String format, String... params) {
        console.info(format, params);
    }

    public static void info(Exception e) {
        console.info(e);
    }
}
