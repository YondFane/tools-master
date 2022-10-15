package com.yfan;

import cn.hutool.setting.Setting;
import com.yfan.enums.ToolType;
import com.yfan.tools.ITool;
import com.yfan.tools.buildproject.BuildProjectTool;
import com.yfan.tools.generator.GeneratorTool;

import java.util.HashMap;

/**
 * @BelongsProject: tools-master
 * @BelongsPackage: PACKAGE_NAME
 * @Description: 工具工厂
 * @Author: YFAN
 * @CreateTime: 2022-10-15 14:35
 * @Version: 1.0
 */
public class ToolsFactory {
    private static HashMap<String, ITool> map = new HashMap<>();

    public static ITool create(String type, Setting setting){
        ITool iTool = null;
        if (ToolType.GENERATOR.getCode().equals(type)) {
            iTool = new GeneratorTool(setting);
        } else if (ToolType.BUILDPROJECT.getCode().equals(type)) {
            iTool = new BuildProjectTool(setting);
        }
        return iTool;
    }
}
