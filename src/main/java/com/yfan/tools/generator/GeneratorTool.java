package com.yfan.tools.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.extra.template.*;
import cn.hutool.setting.Setting;
import com.yfan.tools.AbstractTool;
import com.yfan.tools.ITool;

import javax.sql.DataSource;
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
 * @BelongsProject: tools-master
 * @BelongsPackage: com.yfan.tools.generator
 * @Description: 代码生成器
 * @Author: YFAN
 * @CreateTime: 2022-10-15 14:22
 * @Version: 1.0
 */
public class GeneratorTool extends AbstractTool implements ITool {

    private DataSource dataSource;

    public GeneratorTool(Setting setting) {
        super(setting);
        initDataSource();
    }

    @Override
    public String getConfigGroupName() {
        return "generator";
    }

    private void initDataSource() {
        Setting dbConfig = setting.getSetting("db");
        DSFactory dsFactory = DSFactory.create(dbConfig);
        this.dataSource = dsFactory.getDataSource();
    }

    @Override
    public void excute() throws Exception {
        // 获取模板
        Template template = getTemplate();
        // 多表生成
        String[] tableNames = getConfig("tableName").split(",");
        for (String tableName : tableNames) {
            String fileName = StrUtil.toCamelCase(tableName);
            fileName = Character.toUpperCase(fileName.charAt(0)) + fileName.substring(1);

            File file = new File(getConfig("savepath"), fileName + ".java");
            // 组织参数
            HashMap<String, Object> paramMap = buildParamMap(tableName);
            Console.log("组织参数:{}", paramMap.toString());
            // 生成模板文件
            createTemplateFile(file, template, paramMap);
            Console.log("生成路径为：{}", file.getAbsolutePath());
        }
    }

    /**
     * @description: 构建模板填充参数
     * @author: YFAN
     * @date: 2022/10/15/015 14:54
     * @param: tableName
     * @return: java.util.HashMap<java.lang.String, java.lang.Object>
     **/
    private HashMap<String, Object> buildParamMap(String tableName) throws SQLException {
        Db db = Db.use(dataSource);
        HashMap<String, Object> genMap = new HashMap<>();
        Entity tableInfo = null;
        List<Entity> tableColumns = null;
        String currentDatasource = getConfig("currentDatasource", "db");
        if ("MYSQL".equals(currentDatasource)) {
            tableInfo = db.queryOne(GeneratorQuerySQL.MYSQL_SQL_TABLE_INFO, tableName);
            tableColumns = db.query(GeneratorQuerySQL.MYSQL_SQL_COLUMN_INFO, tableName);
        } else if ("ORACLE".equals(currentDatasource)) {
            tableInfo = db.queryOne(GeneratorQuerySQL.ORACLE_SQL_TABLE_INFO, tableName);
            tableColumns = db.query(GeneratorQuerySQL.ORACLE_SQL_COLUMN_INFO, tableName);
        } else {
            Console.log(new RuntimeException(currentDatasource + "数据库类型不支持！"));
        }
        if (tableInfo == null || tableColumns.size() == 0) {
            Console.log(new RuntimeException("表信息或表字段信息为空！"));
        }
        Console.log("{}表信息：{}", tableName, tableInfo.toString());
        Console.log("{}表字段信息：{}", tableName, tableColumns.toString());
        // 接口别名
        genMap.put("apiAlias", getConfig("apiAlias"));
        // 包名称
        genMap.put("package", getConfig("package"));
        // 包名称+模块
        genMap.put("packageModule", getConfig("package") + getConfig("packModule"));
        // 模块名称
        genMap.put("moduleName", getConfig("moduleName"));
        // 作者
        genMap.put("author", getConfig("author"));
        // 创建日期
        genMap.put("date", LocalDate.now().toString());

        genMap.put("tableName", tableName);
        // 表注释
        if (StrUtil.isBlank((CharSequence) tableInfo.get("tablecomment"))) {
            genMap.put("tableComment", "");
        } else {
            genMap.put("tableComment", tableInfo.get("tablecomment"));
        }
        // 大写开头的类名
        String camelCaseTableName = StrUtil.toCamelCase(tableName);
        String className = Character.toUpperCase(camelCaseTableName.charAt(0)) + camelCaseTableName.substring(1);

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
            String columnName = column.get("columnName").toString();
            // 小写开头的字段名
            String changeColumnName = StrUtil.toCamelCase(columnName);
            // 大写开头的字段名
            String capitalColumnName = Character.toUpperCase(changeColumnName.charAt(0)) + changeColumnName.substring(1);

            // 字段描述
            listMap.put("remark", column.get("columncomment"));
            // 字段类型
            listMap.put("columnKey", column.get("columnKey"));
            // 主键类型
            String colType = cloToJava(column.get("datatype").toString());
            // 是否存在 Timestamp 类型的字段
            if ("Timestamp".equals(colType)) {
                genMap.put("hasTimestamp", true);
            }
            // 是否存在 BigDecimal 类型的字段
            if ("BigDecimal".equals(colType)) {
                genMap.put("hasBigDecimal", true);
            }
            // 主键是否自增
            if ("auto_increment".equals(column.get("extra"))) {
                genMap.put("auto", true);
            }
            // 主键是否自增
            if ("auto_increment".equals(column.get("extra"))) {
                genMap.put("auto", true);
            }
            // 删除标志追加到sql查询语句
            genMap.put("hasWhereIsDeleted", false);
            if ("IS_DELETED".equals(columnName.toUpperCase())) {
                genMap.put("hasWhereIsDeleted", true);
                genMap.put("IsDeleted", columnName);
            }

            // 存储字段类型
            // 如果 columnType == tinyint(1) 那么字段类型为 Boolean
            if ("tinyint(1)".equals(column.get("columnType"))) {
                colType = "Boolean";
            }
            listMap.put("columnType", colType);
            // 存储字原始段名称
            listMap.put("columnName", columnName);
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
     * @description: 生成模板文件
     * @author: YFAN
     * @date: 2022/10/15/015 14:55
     * @param: file
     * @param: template
     * @param: paramMap
     **/
    private void createTemplateFile(File file, Template template, HashMap<String, Object> map) {
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

    /**
     * @description: 获取模板
     * @author: YFAN
     * @date: 2022/10/15/015 14:53
     * @return: cn.hutool.extra.template.Template
     **/
    private Template getTemplate() {
        Template template = null;
        try {
            TemplateConfig templateConfig = new TemplateConfig(new File(getConfig("templatePath")).getAbsolutePath(),
                    TemplateConfig.ResourceMode.FILE);
            TemplateEngine engine = TemplateUtil.createEngine(templateConfig);
            template = engine.getTemplate(getConfig("templateName"));
        } catch (Exception e) {
            Console.log(e);
        }
        if (template == null) {
            Console.log(new RuntimeException("获取不到模板！"));
        }
        return template;
    }

    /**
     * 转换mysql数据类型为java数据类型
     *
     * @param type 数据库字段类型
     * @return String
     */
    private String cloToJava(String type) {
        String result = getConfig(type, "dbTypeToJavaType");
        if (StrUtil.isBlank(result)) {
            return type;
        }
        return result;
    }

}
