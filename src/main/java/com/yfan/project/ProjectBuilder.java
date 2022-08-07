package com.yfan.project;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目构建类
 *
 * @Author: YFAN
 * @CreateTime: 2022-08-07 00:21
 */
public class ProjectBuilder {


    public static void build() {
        // 非项目相关
        buildNoProject();
        // 项目相关
        buildProject();
    }

    /**
     * 非项目相关
     */
    private static void buildNoProject() {
        Console.log("---------------------------非项目相关复制开始---------------------------");
        // 直接复制到保存目录中
        ProjectConfig.noProjectDiretory.forEach(file -> {
            // 覆盖复制
            FileUtil.copyContent(file, new File(ProjectConfig.SAVE_PATH + File.separator + file.getName()), true);
        });
        Console.log("---------------------------非项目相关复制结束---------------------------");
    }

    /**
     * 项目相关
     */
    private static void buildProject() {
        Console.log("---------------------------项目相关复制开始---------------------------");
        // 1、静态文件，静态html页面，css文件，js文件等
        buildProjectByStaticFiles();
        // 2、class文件或配置文件等
        buildProjectByProjectFiles();
        Console.log("---------------------------项目相关复制结束---------------------------");
    }

    /**
     * 项目相关复制
     * 2、class文件或配置文件等
     */
    private static void buildProjectByProjectFiles() {
        Console.log("---------------------------项目相关复制：2、class文件或配置文件等----复制开始---------------------------");
        ProjectConfig.projectDiretory.forEach(projectDiretoy -> {
            // 创建WEB-INF目录
            File webInfPath = new File(ProjectConfig.SAVE_PATH, projectDiretoy.getName() + "/WEB-INF");
            if (!webInfPath.exists()) {
                webInfPath.mkdirs();
            }
            // 1、java文件===>class
            buildSrcMainJavaPath(projectDiretoy);
            // 2、config文件===>resource
            buildSrcMainResources(projectDiretoy);
            // 3、lib文件 jar包
            buildWebInfLibPath(projectDiretoy);
        });
        Console.log("---------------------------项目相关复制：2、class文件或配置文件等----复制结束---------------------------");
    }

    /**
     * 构建/WEB-INF/lib
     * @param projectDiretoy
     */
    private static void buildWebInfLibPath(File projectDiretoy) {
        Console.log("========lib文件 jar包");
        if (ProjectConfig.jarList.size() > 0) {
            // 创建WEB-INF/lib目录
            File webInfLibPath = new File(ProjectConfig.SAVE_PATH, projectDiretoy.getName() + "/WEB-INF/lib");
            if (!webInfLibPath.exists()) {
                webInfLibPath.mkdirs();
            }
            // C:\Users\YFAN\Desktop\gdjd-xunjia-root\gdjd-xunjia\target\gdjd-xunjia\WEB-INF\lib
            // 对应工作空间存在jar包的lib目录
            File projectLibWorkSpacePath = new File(ProjectConfig.WORKSPACE_PATH,
                    projectDiretoy.getName()+"/target/" + projectDiretoy.getName() + "/WEB-INF/lib");
            if (projectLibWorkSpacePath.exists()) {
                for (File jarFile : projectLibWorkSpacePath.listFiles()) {
                    String jarFileName = jarFile.getName();
                    for (String mudules : ProjectConfig.jarList) {
                        if (jarFileName.contains(mudules)) {
                            FileUtil.copy(jarFile, webInfLibPath, true);
                            Console.log("\t\t{}复制成功", jarFile.getAbsolutePath());
                            break;
                        }
                    }
                }
            } else {
                Console.log("{}找不到路径", projectLibWorkSpacePath.getAbsolutePath());
            }
        }
    }

    /**
     * 构建配置文件/src/main/resources
     * @param projectDiretoy
     */
    private static void buildSrcMainResources(File projectDiretoy) {
        Console.log("========/src/main/resources");
        File configPath = new File(projectDiretoy, "/src/main/resources");
        if (FileUtil.exist(configPath)) {
            // 创建WEB-INF/classes目录
            File webInfClassPath = new File(ProjectConfig.SAVE_PATH, projectDiretoy.getName() + "/WEB-INF/classes");
            if (!webInfClassPath.exists()) {
                webInfClassPath.mkdirs();
            }
            List<File> configPathFiles = new ArrayList<>();
            dfsDirectory(configPathFiles, configPath);
            configPathFiles.forEach(configPathFile -> {
                String tFileName = configPathFile.getName();
                Console.log("\tFileName:{},FilePath:{}", tFileName, configPathFile.getAbsolutePath());
                if (tFileName.endsWith(".sql")) {
                    // sql文件复制到保存目录下
                    Console.log("\tsql文件复制到SAVE_PATH目录下");
                    FileUtil.copy(configPathFile, new File(ProjectConfig.SAVE_PATH), true);
                } else if (configPathFile.getAbsolutePath().contains("i18n")) {
                    // i18n国际化文件
                    // 创建对应的目录再复制
                    Console.log("\ti18n文件");
                    File i18nPath = new File(webInfClassPath, "i18n");
                    if (!i18nPath.exists()) {
                        i18nPath.mkdirs();
                    }
                    FileUtil.copy(configPathFile, i18nPath, true);

                } else if (tFileName.indexOf(".") == -1) {
                    // 无后缀文件
                    Console.log("\t无后缀文件");
                    FileUtil.copy(configPathFile, new File(ProjectConfig.SAVE_PATH), true);
                } else {
                    FileUtil.copy(configPathFile, webInfClassPath, true);
                }
                Console.log("\t\t{}/{}复制成功", webInfClassPath.getAbsolutePath(), configPathFile.getName());
            });
        }
    }

    /**
     * 构建/src/main/java
     * @param projectDiretoy
     */
    private static void buildSrcMainJavaPath(File projectDiretoy) {
        Console.log("========/src/main/java");
        File javaPath = new File(projectDiretoy, "/src/main/java");
        if (FileUtil.exist(javaPath)) {
            // 创建WEB-INF/classes目录
            File webInfClassPath = new File(ProjectConfig.SAVE_PATH, projectDiretoy.getName() + "/WEB-INF/classes");
            webInfClassPath.mkdirs();
            List<File> javaDirectoryFiles = new ArrayList<>();
            dfsDirectory(javaDirectoryFiles, javaPath);
            javaDirectoryFiles.forEach(javaDirectoryFile -> {
                String tFileName = javaDirectoryFile.getName();
                Console.log("\tFileName:{},FilePath:{}", tFileName, javaDirectoryFile.getAbsolutePath());
                // 截取/src/main/java后的全路径
                String absolutePath = javaDirectoryFile.getAbsolutePath();
                absolutePath = absolutePath.substring(absolutePath.indexOf("com"));
                Console.log("\tabsolutePath:{}", absolutePath);
                // java文件
                if (tFileName.endsWith(".java")) {
                    String classFileAbsolutePath = absolutePath.replaceAll(".java", ".class");
                    // class文件是否在工作空间
                    File classFilePath = new File(ProjectConfig.WORKSPACE_PATH,
                            projectDiretoy.getName() + "/target/classes/" + classFileAbsolutePath);
                    if (classFilePath.exists()) {
                        Console.log("\tclassFilePath:{}存在", classFilePath.getAbsolutePath());
                        File saveClassPath = new File(webInfClassPath, classFileAbsolutePath);
                        FileUtil.copy(classFilePath, saveClassPath, true);
                        Console.log("\t\t{}复制成功", saveClassPath.getAbsolutePath());
                    } else {
                        Console.log(new RuntimeException("classFilePath不存在:" + classFilePath.getAbsolutePath()));
                    }
                } else {
                    //非java文件
                    //TODO 目前还没遇到
                }
            });
        }
    }


    /**
     * 深度遍历目录下的文件
     *
     * @param fileList
     * @param diretory
     */
    private static void dfsDirectory(List<File> fileList, File diretory) {
        if (diretory.isDirectory()) {
            for (File file : diretory.listFiles()) {
                dfsDirectory(fileList, file);
            }
        } else {
            fileList.add(diretory);
            Console.log("\t遍历到文件：fileName:{},filePath:{}", diretory.getName(), diretory.getAbsolutePath());
        }
    }


    /**
     * 项目相关
     * 1、静态文件，静态html页面，css文件，js文件等
     */
    private static void buildProjectByStaticFiles() {
        Console.log("---------------------------项目相关复制：1、静态文件，静态html页面，css文件，js文件等----复制开始---------------------------");
        ProjectConfig.projectDiretory.forEach(file -> {
            // 判断是否存在webapp目录
            File webappPath = new File(file, "/src/main/webapp");
            if (FileUtil.exist(webappPath)) {
                // 存在webapp目录
                Console.log("\t\t{}存在webapp目录", file.getName());
                for (File listFile : webappPath.listFiles()) {
                    if (listFile.isDirectory()) {
                        Console.log("\twebapp目录下的目录名{}，目录全路径{}", listFile.getName(), listFile.getAbsolutePath());
                        continue;
                    }
                    Console.log("\twebapp目录下的文件名{}，文件全路径{}", listFile.getName(), listFile.getAbsolutePath());
                }
                Console.log("---------------------------webapp目录复制开始---------------------------");
                FileUtil.copyContent(webappPath, new File(ProjectConfig.SAVE_PATH + File.separator + file.getName()), true);
                Console.log("---------------------------webapp目录复制结束---------------------------");
            } else {
                Console.log("\t\t{}不存在webapp目录", file.getName());
            }
        });
        Console.log("---------------------------项目相关复制：1、静态文件，静态html页面，css文件，js文件等----复制结束---------------------------");
    }


}
