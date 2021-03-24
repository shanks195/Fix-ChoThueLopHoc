package ${package}.dto;

import lombok.Data;
<#if hasTimestamp>
import java.sql.Timestamp;
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>
import java.io.Serializable;
<#if !auto && pkColumnType = 'Long'>
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
</#if>



@Data
public class ${className}Dto implements Serializable {
<#if columns??>
    <#list columns as column>
        <#if column.remark != ''>
    // ${column.remark}
        </#if>
        <#if column.columnKey = 'PRI'>
            <#if !auto && pkColumnType = 'Long'>
    @JsonSerialize(using= ToStringSerializer.class)
            </#if>
        </#if>
    private ${column.columnType} ${column.changeColumnName};
    </#list>
</#if>
}
