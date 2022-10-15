package com.yfan.tools;

import cn.hutool.setting.Setting;

/**
 * @BelongsProject: tools-master
 * @BelongsPackage: com.yfan.tools
 * @Description: 工具抽象层
 * @Author: YFAN
 * @CreateTime: 2022-10-15 14:18
 * @Version: 1.0
 */
public abstract class AbstractTool {

    protected Setting setting;

    public AbstractTool(Setting setting) {
        this.setting = setting;
    }

    public abstract String getConfigGroupName();

    public String getConfig(String key) {
        return setting.getByGroup(key, getConfigGroupName());
    }

    public String getConfig(String key, String groupName) {
        return setting.getByGroup(key, groupName);
    }


    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}
