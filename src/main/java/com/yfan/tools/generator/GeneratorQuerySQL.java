package com.yfan.tools.generator;

/**
 * 代码生成器配置
 * @Author: YFAN
 * @CreateTime: 2022-08-07 14:58
 */
public class GeneratorQuerySQL {

    public static final String MYSQL_SQL_TABLE_INFO =
            " select " +
            "   table_name tableName, " +
            "   engine, " +
            "   table_comment tableComment, " +
            "   create_time createTime " +
            " from information_schema.tables " +
            " where table_schema = (select database()) and table_name = ? ";

    public static final String MYSQL_SQL_COLUMN_INFO =
            " select column_name columnName, " +
            "   data_type dataType, " +
            "   column_comment columnComment, " +
            "   column_key columnKey, " +
            "   extra, " +
            "   COLUMN_TYPE columnType" +
            " from information_schema.columns " +
            " where table_name = ? " +
            " and table_schema = (select database()) order by ordinal_position ";

    public static final String ORACLE_SQL_TABLE_INFO =
            " select dt.table_name tableName, " +
            "   dtc.comments tableComment, " +
            "   dt.last_analyzed createTime " +
            " from user_tables dt,user_tab_comments dtc " +
            " where dt.table_name=dtc.table_name and dt.table_name = UPPER(?) ";

    public static final String ORACLE_SQL_COLUMN_INFO =
            "select temp.column_name columnName, " +
            "   temp.data_type dataType," +
            "   nvl(temp.comments,' ') columnComment, " +
            "   case temp.constraint_type when 'P' then 'PRI' when 'C' then 'UNI' else ' ' end columnKey, " +
            "   'NULL' EXTRA, " +
            "   'NULL' columnType " +
            "from ( " +
            "   select col.column_id, " +
            "   col.column_name, " +
            "   col.data_type, " +
            "   colc.comments, " +
            "   uc.constraint_type, " +
            "   row_number() over (partition by col.column_name order by uc.constraint_type desc) as row_flg " +
            "   from user_tab_columns col " +
            "   left join user_col_comments colc " +
            "   on colc.table_name = col.table_name " +
            "   and colc.column_name = col.column_name " +
            "   left join user_cons_columns ucc " +
            "   on ucc.table_name = col.table_name " +
            "   and ucc.column_name = col.column_name " +
            "   left join user_constraints uc " +
            "   on uc.constraint_name = ucc.constraint_name " +
            "   where col.table_name = upper(?) " +
            "   ) temp " +
            "where temp.row_flg = 1 " +
            "order by temp.column_id";
}
