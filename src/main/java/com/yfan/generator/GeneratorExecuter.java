package com.yfan.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.extra.template.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器执行者
 *
 * @Author: YFAN
 * @CreateTime: 2022-08-07 16:16
 */
public class GeneratorExecuter {

    private static final String TIMESTAMP = "Timestamp";

    private static final String BIGDECIMAL = "BigDecimal";

    // 主键
    public static final String PK = "PRI";
    // mysql自增
    public static final String EXTRA = "auto_increment";

    /**
     * 执行
     *
     * @throws SQLException
     * @throws IOException
     */
    public static void execute() throws SQLException, IOException {
        Console.log("=======================代码生成器执行开始=================");
        GeneratorConfig.initConfig();
        Db db = GeneratorConfig.db();
        // 获取模板
        Template template = getTemplate();
        String fileName = Character.toUpperCase(GeneratorConfig.tableName.charAt(0)) + GeneratorConfig.tableName.substring(1);
        File file = new File(GeneratorConfig.generatorPath, fileName + ".java");
        // 组织参数
        HashMap<String, Object> paramMap = buildParamMap(db);
        Console.log("组织参数:{}", paramMap.toString());
        // 生成模板文件
        createTemplateFile(file, template, paramMap);
        Console.log("生成路径为：{}", file.getAbsolutePath());
        Console.log("=======================代码生成器执行结束=================");
    }

    /**
     * 组织参数
     *
     * @return
     */
    private static HashMap<String, Object> buildParamMap(Db db) throws SQLException {
        HashMap<String, Object> genMap = new HashMap<>();
        Entity tableInfo = null;
        List<Entity> tableColumns = null;
        if ("MYSQL".equals(GeneratorConfig.currentDatasource)) {
            tableInfo = db.queryOne(GeneratorConfig.MYSQL_SQL_TABLE_INFO, GeneratorConfig.tableName);
            tableColumns = db.query(GeneratorConfig.MYSQL_SQL_COLUMN_INFO, GeneratorConfig.tableName);
        } else if ("ORACLE".equals(GeneratorConfig.currentDatasource)) {
            tableInfo = db.queryOne(GeneratorConfig.ORACLE_SQL_TABLE_INFO, GeneratorConfig.tableName);
            tableColumns = db.query(GeneratorConfig.ORACLE_SQL_COLUMN_INFO, GeneratorConfig.tableName);
        } else {
            Console.log(new RuntimeException(GeneratorConfig.currentDatasource + "数据库类型不支持！"));
        }

        Console.log("{}表信息：{}", GeneratorConfig.tableName, tableInfo.toString());
        Console.log("{}表字段信息：{}", GeneratorConfig.tableName, tableColumns.toString());
        // 接口别名
        genMap.put("apiAlias", GeneratorConfig.apiAlias);
        // 包名称
        genMap.put("package", GeneratorConfig.pack);
        // 包名称+模块
        genMap.put("packageModule", GeneratorConfig.packModule);
        // 模块名称
        genMap.put("moduleName", GeneratorConfig.moduleName);
        // 作者
        genMap.put("author", GeneratorConfig.author);
        // 创建日期
        genMap.put("date", LocalDate.now().toString());
        // 表名
        String tableName = tableInfo.getTableName();
        genMap.put("tableName", tableName.toUpperCase());
        // 表注释
        if (StrUtil.isBlank((CharSequence) tableInfo.get("tablecomment"))) {
            genMap.put("tableComment", "");
        } else {
            genMap.put("tableComment", tableInfo.get("tablecomment"));
        }
        // 大写开头的类名
        String className = Character.toUpperCase(tableName.charAt(0)) + tableName.substring(1);

        // 保存类名
        genMap.put("className", className);
        // 存在 Timestamp 字段
        genMap.put("hasTimestamp", false);
        // 存在 BigDecimal 字段
        genMap.put("hasBigDecimal", false);
        // 自增主键
        genMap.put("auto", false);
        // 存在日期注解
        genMap.put("hasDateAnnotation", false);
        // 字段信息
        // 保存字段信息
        List<Map<String, Object>> columns = new ArrayList<>();
        tableColumns.forEach(column -> {
            Map<String, Object> listMap = new HashMap<>();
            String columnname = column.get("columnname").toString();
            // 小写开头的字段名
            String changeColumnName = StrUtil.toCamelCase(columnname);
            // 大写开头的字段名
            String capitalColumnName = Character.toUpperCase(changeColumnName.charAt(0)) + changeColumnName.substring(1);

            // 字段描述
            listMap.put("remark", column.get("columncomment"));
            // 字段类型
            listMap.put("columnKey", column.get("datatype"));
            // 主键类型
            String colType = GeneratorConfig.cloToJava(column.get("datatype").toString());
            // 是否存在 Timestamp 类型的字段
            if (TIMESTAMP.equals(colType)) {
                genMap.put("hasTimestamp", true);
            }
            // 是否存在 BigDecimal 类型的字段
            if (BIGDECIMAL.equals(colType)) {
                genMap.put("hasBigDecimal", true);
            }
            // 主键是否自增
            if (EXTRA.equals(column.get("extra"))) {
                genMap.put("auto", true);
            }
            // 主键是否自增
            if (EXTRA.equals(column.get("extra"))) {
                genMap.put("auto", true);
            }
            // 存储字段类型
            listMap.put("columnType", colType);
            // 存储字原始段名称
            listMap.put("columnName", columnname);
            // 不为空
            listMap.put("istNotNull", false);
            // 小写开头的字段名称
            listMap.put("changeColumnName", changeColumnName);
            // 日期注解 TODO
            listMap.put("dateAnnotation", "");
            genMap.put("hasDateAnnotation", false);
            // 添加到字段列表中
            columns.add(listMap);
        });
        // 保存字段列表
        genMap.put("columns", columns);
        return genMap;
    }

    /**
     * 获取模板
     */
    private static Template getTemplate() {
        Template template = null;
        try {
            TemplateConfig templateConfig = new TemplateConfig(new File(GeneratorConfig.templatePath).getAbsolutePath(), TemplateConfig.ResourceMode.FILE);
            TemplateEngine engine = TemplateUtil.createEngine(templateConfig);
            template = engine.getTemplate(GeneratorConfig.templateName);
        } catch (Exception e) {
            Console.log(e);
        }
        if (template == null) {
            Console.log(new RuntimeException("获取不到模板！"));
        }
        return template;
    }

    /**
     * 生成模板文件
     *
     * @param file
     * @param template
     * @param map
     * @throws IOException
     */
    private static void createTemplateFile(File file, Template template, Map<String, Object> map) throws IOException {
        // 生成目标文件
        Writer writer = null;
        try {
            FileUtil.touch(file);
            writer = new FileWriter(file);
            template.render(map, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            IoUtil.close(writer);
        }
    }


}
