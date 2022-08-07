package com.yfan.project;

import com.yfan.console.Console;

import java.io.File;

/**
 * 项目扫描类
 *
 * @Author: YFAN
 * @CreateTime: 2022-08-06 23:58
 */
public class ProjectScanner {

    public static void scanner() {
        Console.info("============================项目目录扫描开始============================");
        File[] sourceFiles = new File(ProjectConfig.SOURCE_PATH).listFiles();
        for (File file : sourceFiles) {
            String fileName = file.getName();
            Console.info(fileName);
            if (file.isDirectory()) {
                Console.info("扫描到项目目录下的目录:{},全路径为：{}", fileName, file.getAbsolutePath());
                if (fileName.startsWith(ProjectConfig.projectName)) {
                    if (ProjectConfig.commonModules.contains(fileName)) {
                        Console.info("\t\t该目录{}为公共组件Jar目录", fileName);
                        ProjectConfig.jarList.add(fileName);
                    } else {
                        Console.info("\t\t该目录{}为项目目录而非公共组件", fileName);
                        ProjectConfig.projectDiretory.add(file);
                    }
                } else {
                    Console.info("\t\t该目录{}为公共组件目录", fileName);
                    ProjectConfig.noProjectDiretory.add(file);
                }
            } else {
                // 项目目录下的文件不做处理，大概为README.md等文件
                Console.info("扫描到项目目录下的文件:{}" + fileName);
            }
        }
        Console.info("============================项目目录扫描结束============================");
        Console.info("============================项目目录扫描结果============================");
        ProjectConfig.projectDiretory.forEach(file -> {
            Console.info("\t\t扫描到项目目录:{},全路径为：{}", file.getName(), file.getAbsolutePath());
        });
        ProjectConfig.jarList.forEach(jarStr -> {
            Console.info("\t\t扫描到Jar文件名:{}", jarStr );
        });
        ProjectConfig.noProjectDiretory.forEach(file -> {
            Console.info("\t\t扫描到公共组件目录:{},全路径为：{}", file.getName(), file.getAbsolutePath());
        });
        Console.info("============================项目目录扫描结果============================");
    }

}
