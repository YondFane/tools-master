package com.yfan.project;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.yfan.config.Config;
import com.yfan.console.Console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目配置类
 *
 * @Author: YFAN
 * @CreateTime: 2022-08-06 23:27
 */
public class ProjectConfig {

    private static Setting config;

    // 项目名
    public static String projectName;
    // 公共组件名
    public static List<String> commonModules;
    // 源码目录
    public static String SOURCE_PATH;
    // 工作空间
    public static String WORKSPACE_PATH;
    // 保存目录
    public static String SAVE_PATH;

    // 项目目录
    public static List<File> projectDiretory = new ArrayList<>();
    // 非项目目录 公共组件dao entity service datasql等
    public static List<File> noProjectDiretory = new ArrayList<>();
    // 存在jar模糊名称
    public static List<String> jarList = new ArrayList<>();

    public static void setConfig(Setting tConfig) {
        config = tConfig;
    }

    /**
     * 初始化配置
     */
    public static void initConfig() {
        if (config == null) {
            config = Config.getConfig();
        }
        projectName = config.get("projectName");
        SOURCE_PATH = config.get("sourcePath");
        WORKSPACE_PATH = config.get("workspacePath");
        SAVE_PATH = config.get("savePath");
        String[] commonPaths = config.get("commonModules").split(",");

        check();

        // 公共组件
        commonModules = new ArrayList<>();
        for (String s : commonPaths) {
            commonModules.add(projectName + "-" + s);
        }
        Console.info("公共组件配置：{}", commonModules.toString());

    }

    /**
     * 校验路径
     */
    private static void check() {
        if (StrUtil.isBlank(projectName)) {
            Console.info(new RuntimeException("项目名不能为空"));
        } else {
            Console.info("项目名:{}", projectName);
        }
        if (StrUtil.isBlank(SOURCE_PATH)) {
            Console.info(new RuntimeException("源码目录不能为空"));
        } else {
            Console.info("源码目录:{}", projectName);
            if (!FileUtil.exist(SOURCE_PATH)) {
                Console.info(new RuntimeException("源码目录不存在！"));
            }
        }
        if (StrUtil.isBlank(WORKSPACE_PATH)) {
            Console.info(new RuntimeException("工作空间不能为空"));
        } else {
            Console.info("工作空间:{}", WORKSPACE_PATH);
            if (!FileUtil.exist(WORKSPACE_PATH)) {
                Console.info(new RuntimeException("工作空间不存在！"));
            }
        }
        if (StrUtil.isBlank(SAVE_PATH)) {
            throw new RuntimeException("保存目录不能为空");
        } else {
            Console.info("保存目录:{}", SAVE_PATH);
            if (!FileUtil.exist(SAVE_PATH)) {
                Console.info(new RuntimeException("保存目录不存在！"));
            }
        }
    }
}
