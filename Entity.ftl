package ${packageModule}.entity;

import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
<#if hasDateAnnotation>
import org.hibernate.annotations.*;
</#if>
<#if hasTimestamp>
import java.sql.Timestamp;
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>
<#if auto>
import org.hibernate.annotations.GenericGenerator;
</#if>


/**
*
* @description ${tableComment}
* @author ${author}
* @date ${date}
**/
@Entity
@Data
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="${tableName}")
<#if hasWhereIsDeleted>
@Where(clause="${IsDeleted}=0")
</#if>
public class ${className} implements Serializable {
<#if columns??>
    <#list columns as column>

    /**
        ${column.remark}
     */
    <#if column.columnKey = 'PRI'>
    @Id
    <#if auto>
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
    </#if>
    @Column(name = "${column.columnName}"<#if column.columnKey = 'UNI'>,unique = true</#if><#if column.istNotNull && column.columnKey != 'PRI'>,nullable = false</#if>)
    <#if column.remark != ''>
    @ApiModelProperty(value = "${column.remark}")
    <#else>
    @ApiModelProperty(value = "${column.changeColumnName}")
    </#if>
    private ${column.columnType} ${column.changeColumnName};
    </#list>
</#if>

}
