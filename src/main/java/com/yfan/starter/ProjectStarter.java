package com.yfan.starter;

import com.yfan.project.ProjectBuilder;
import com.yfan.project.ProjectConfig;
import com.yfan.project.ProjectScanner;

/**
 * 项目启动器
 * @Author: YFAN
 * @CreateTime: 2022-08-07 11:16
 */
public class ProjectStarter implements Starter{

    @Override
    public void start() {
        // 初始化配置
        ProjectConfig.initConfig();
        // 扫描源码目录
        ProjectScanner.scanner();
        // 构建项目
        ProjectBuilder.build();
    }

}
