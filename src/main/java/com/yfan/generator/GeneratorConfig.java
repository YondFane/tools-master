package com.yfan.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import com.yfan.config.Config;

import javax.sql.DataSource;

/**
 * 代码生成器配置
 *
 * @Author: YFAN
 * @CreateTime: 2022-08-07 14:58
 */
public class GeneratorConfig {

    public static final String MYSQL_SQL_TABLE_INFO =
            " select table_name tableName, engine, table_comment tableComment, create_time createTime " +
                    " from information_schema.tables " +
                    " where table_schema = (select database()) and table_name = ? ";

    public static final String MYSQL_SQL_COLUMN_INFO =
            " select column_name columnName, data_type dataType, " +
                    "   column_comment columnComment, " +
                    "   column_key columnKey, extra " +
                    " from information_schema.columns " +
                    " where table_name = ? " +
                    " and table_schema = (select database()) order by ordinal_position ";

    public static final String ORACLE_SQL_TABLE_INFO =
            " select dt.table_name tableName,dtc.comments tableComment,dt.last_analyzed createTime " +
            " from user_tables dt,user_tab_comments dtc " +
            " where dt.table_name=dtc.table_name and dt.table_name = UPPER(?) ";

    public static final String ORACLE_SQL_COLUMN_INFO =
            "select temp.column_name columnname, " +
            "        temp.data_type dataType," +
            "        nvl(temp.comments,' ') columnComment, " +
            "        case temp.constraint_type when 'P' then 'PRI' when 'C' then 'UNI' else ' ' end COLUMNKEY, " +
            "        'NULL' EXTRA " +
            "        from ( " +
            "        select col.column_id, " +
            "        col.column_name, " +
            "        col.data_type, " +
            "        colc.comments, " +
            "        uc.constraint_type, " +
            "        row_number() over (partition by col.column_name order by uc.constraint_type desc) as row_flg " +
            "        from user_tab_columns col " +
            "        left join user_col_comments colc " +
            "        on colc.table_name = col.table_name " +
            "        and colc.column_name = col.column_name " +
            "        left join user_cons_columns ucc " +
            "        on ucc.table_name = col.table_name " +
            "        and ucc.column_name = col.column_name " +
            "        left join user_constraints uc " +
            "        on uc.constraint_name = ucc.constraint_name " +
            "        where col.table_name = upper(?) " +
            "        ) temp " +
            "        where temp.row_flg = 1 " +
            "        order by temp.column_id";

    private static Setting config;

    public static String templatePath = "";

    public static String templateName;

    public static String currentDatasource;

    public static DataSource dataSource;

    // 表名
    public static String tableName;

    // 接口名称
    public static String apiAlias;

    // 包路径
    public static String pack;

    // 包路径+模块路径
    public static String packModule;

    // 模块名
    public static String moduleName;

    // 作者
    public static String author;

    // 表前缀
    public static String prefix;
    // 是否覆盖
    public Boolean cover = true;

    public static String generatorPath;

    public static void setConfig(Setting tConfig) {
        config = tConfig;
    }

    /**
     * 初始化配置
     * 数据源
     */
    public static void initConfig() {
        if (config == null) {
            config = Config.getConfig();
            Console.log("配置文件：\n{}",config);
        }
        currentDatasource = config.get("currentDatasource");
        String templatePath2 = config.get("templatePath");
        if (StrUtil.isNotBlank(templatePath2)) {
            if (FileUtil.exist(templatePath2)) {
                templatePath = templatePath2;
                Console.log("=============模板目录为：{}", templatePath);
            } else {
                Console.log(new RuntimeException("模板路径不存在"));
            }
        }
        String templateName2 = config.get("templateName");
        if (StrUtil.isBlank(templateName2)) {
            Console.log(new RuntimeException("模板没有配置"));
        } else {
            templateName = templateName2;
            Console.log("=============模板为：{}", templateName);

        }
        /*if ("".equals(templatePath) && FileUtil.exist(templateName2)) {
            templateName = templateName2;
            Console.log("=============模板路径为：{}", FileUtil.getAbsolutePath(templatePath2));

        } else if (!templatePath.equals("") && FileUtil.exist(templatePath, templateName2)) {
            templateName = templateName2;
            Console.log("=============模板路径为：{}/{}", templatePath, templateName);
        } else {
            Console.log(new RuntimeException("模板不存在"));
        }*/
        DSFactory dsFactory = DSFactory.create(config);
        dataSource = dsFactory.getDataSource();
        // 表名
        tableName = config.get("tableName");
        // 包配置以及注释信息
        author = config.get("genconfig.author");
        pack = config.get("genconfig.pack");
        apiAlias = config.get("genconfig.apiAlias");
        moduleName = config.get("genconfig.moduleName");
        prefix = config.get("genconfig.prefix");
        packModule = config.get("genconfig.packModule");
        if (StrUtil.isBlank(packModule)) {
            packModule = apiAlias;
        }
        // 生成保存路径
        generatorPath = config.get("generator.path");
        if (FileUtil.exist(generatorPath)) {
            Console.log("=============模板生成保存路径为：{}", generatorPath);
        } else {
            Console.log(new RuntimeException("模板生成保存路径不存在"));
        }
    }

    /**
     * 获取数据库操作类实例
     *
     * @return
     */
    public static Db db() {
        return new Db(dataSource);
    }

    /**
     * 转换mysql数据类型为java数据类型
     *
     * @param type 数据库字段类型
     * @return String
     */
    public static String cloToJava(String type) {
        String result = config.get(type);
        if (StrUtil.isBlank(result)) {
            return type;
        }
        return result;
    }
}
