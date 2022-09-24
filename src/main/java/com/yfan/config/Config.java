package com.yfan.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.Setting;

import java.io.File;

/**
 * 配置文件
 * @Author: YFAN
 * @CreateTime: 2022-08-07 11:10
 */
public class Config {
    private static final String CONFIG_PATH = "config.properties";

    public static Setting getConfig(){
        File config = FileUtil.file(CONFIG_PATH);
        if (!FileUtil.exist(config)) {
            config = FileUtil.file(System.getProperty("user.dir"), CONFIG_PATH);
            if (!FileUtil.exist(config)) {
                return null;
            }
        }
        return new Setting(config, Setting.DEFAULT_CHARSET, false);
    }

}
