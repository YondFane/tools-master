package com.yfan.tools.buildproject;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.yfan.tools.AbstractTool;
import com.yfan.tools.ITool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目启动器
 * @Author: YFAN
 * @CreateTime: 2022-08-07 11:16
 */
public class BuildProjectTool extends AbstractTool implements ITool {

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


    public BuildProjectTool(Setting setting) {
        super(setting);
    }

    @Override
    public String getConfigGroupName() {
        return "buildproject";
    }

    @Override
    public void excute() {
        initConfig();
        // 扫描源码目录
        scanner();
        // 构建项目
        build();
    }

    public void build() {
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
        noProjectDiretory.forEach(file -> {
            // 覆盖复制
            FileUtil.copyContent(file, new File(SAVE_PATH + File.separator + file.getName()), true);
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
        projectDiretory.forEach(projectDiretoy -> {
            // 创建WEB-INF目录
            File webInfPath = new File(SAVE_PATH, projectDiretoy.getName() + "/WEB-INF");
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
        if (jarList.size() > 0) {
            // 创建WEB-INF/lib目录
            File webInfLibPath = new File(SAVE_PATH, projectDiretoy.getName() + "/WEB-INF/lib");
            if (!webInfLibPath.exists()) {
                webInfLibPath.mkdirs();
            }
            // C:\Users\YFAN\Desktop\gdjd-xunjia-root\gdjd-xunjia\target\gdjd-xunjia\WEB-INF\lib
            // 对应工作空间存在jar包的lib目录
            File projectLibWorkSpacePath = new File(WORKSPACE_PATH,
                    projectDiretoy.getName()+"/target/" + projectDiretoy.getName() + "/WEB-INF/lib");
            if (projectLibWorkSpacePath.exists()) {
                for (File jarFile : projectLibWorkSpacePath.listFiles()) {
                    String jarFileName = jarFile.getName();
                    for (String mudules : jarList) {
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
            File webInfClassPath = new File(SAVE_PATH, projectDiretoy.getName() + "/WEB-INF/classes");
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
                    FileUtil.copy(configPathFile, new File(SAVE_PATH), true);
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
                    FileUtil.copy(configPathFile, new File(SAVE_PATH), true);
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
            File webInfClassPath = new File(SAVE_PATH, projectDiretoy.getName() + "/WEB-INF/classes");
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
                    File classFilePath = new File(WORKSPACE_PATH,
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
        projectDiretory.forEach(file -> {
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
                FileUtil.copyContent(webappPath, new File(SAVE_PATH + File.separator + file.getName()), true);
                Console.log("---------------------------webapp目录复制结束---------------------------");
            } else {
                Console.log("\t\t{}不存在webapp目录", file.getName());
            }
        });
        Console.log("---------------------------项目相关复制：1、静态文件，静态html页面，css文件，js文件等----复制结束---------------------------");
    }

    public void scanner() {
        Console.log("============================项目目录扫描开始============================");
        File[] sourceFiles = new File(SOURCE_PATH).listFiles();
        for (File file : sourceFiles) {
            String fileName = file.getName();
            Console.log(fileName);
            if (file.isDirectory()) {
                Console.log("扫描到项目目录下的目录:{},全路径为：{}", fileName, file.getAbsolutePath());
                if (fileName.startsWith(projectName)) {
                    if (commonModules.contains(fileName)) {
                        Console.log("\t\t该目录{}为公共组件Jar目录", fileName);
                        jarList.add(fileName);
                    } else {
                        Console.log("\t\t该目录{}为项目目录而非公共组件", fileName);
                        projectDiretory.add(file);
                    }
                } else {
                    Console.log("\t\t该目录{}为公共组件目录", fileName);
                    noProjectDiretory.add(file);
                }
            } else {
                // 项目目录下的文件不做处理，大概为README.md等文件
                Console.log("扫描到项目目录下的文件:{}" + fileName);
            }
        }
        Console.log("============================项目目录扫描结束============================");
        Console.log("============================项目目录扫描结果============================");
        projectDiretory.forEach(file -> {
            Console.log("\t\t扫描到项目目录:{},全路径为：{}", file.getName(), file.getAbsolutePath());
        });
        jarList.forEach(jarStr -> {
            Console.log("\t\t扫描到Jar文件名:{}", jarStr );
        });
        noProjectDiretory.forEach(file -> {
            Console.log("\t\t扫描到公共组件目录:{},全路径为：{}", file.getName(), file.getAbsolutePath());
        });
        Console.log("============================项目目录扫描结果============================");
    }
    /**
     * 初始化配置
     */
    public void initConfig() {
        projectName = getConfig("projectName");
        SOURCE_PATH = getConfig("sourcePath");
        WORKSPACE_PATH = getConfig("workspacePath");
        SAVE_PATH = getConfig("savePath");
        String[] commonPaths = getConfig("commonModules").split(",");

        check();

        // 公共组件
        commonModules = new ArrayList<>();
        for (String s : commonPaths) {
            commonModules.add(projectName + "-" + s);
        }
        Console.log("公共组件配置：{}", commonModules.toString());

    }

    /**
     * 校验路径
     */
    private static void check() {
        if (StrUtil.isBlank(projectName)) {
            Console.log(new RuntimeException("项目名不能为空"));
        } else {
            Console.log("项目名:{}", projectName);
        }
        if (StrUtil.isBlank(SOURCE_PATH)) {
            Console.log(new RuntimeException("源码目录不能为空"));
        } else {
            Console.log("源码目录:{}", projectName);
            if (!FileUtil.exist(SOURCE_PATH)) {
                Console.log(new RuntimeException("源码目录不存在！"));
            }
        }
        if (StrUtil.isBlank(WORKSPACE_PATH)) {
            Console.log(new RuntimeException("工作空间不能为空"));
        } else {
            Console.log("工作空间:{}", WORKSPACE_PATH);
            if (!FileUtil.exist(WORKSPACE_PATH)) {
                Console.log(new RuntimeException("工作空间不存在！"));
            }
        }
        if (StrUtil.isBlank(SAVE_PATH)) {
            throw new RuntimeException("保存目录不能为空");
        } else {
            Console.log("保存目录:{}", SAVE_PATH);
            if (!FileUtil.exist(SAVE_PATH)) {
                Console.log(new RuntimeException("保存目录不存在！"));
            }
        }
    }
}
