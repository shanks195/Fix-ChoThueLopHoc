package ${package}.query;

import lombok.Data;
<#if queryHasTimestamp>
import java.sql.Timestamp;
</#if>
<#if queryHasBigDecimal>
import java.math.BigDecimal;
</#if>
<#if betweens??>
import java.util.List;
</#if>
<#if queryColumns??>
import vn.thuephonghoc.annotation.Query;
</#if>

@Data
public class ${className}QueryCriteria{
<#if queryColumns??>
    <#list queryColumns as column>

<#if column.queryType = '='>
    @Query
    private ${column.columnType} ${column.changeColumnName};
</#if>
<#if column.queryType = 'Like'>

    @Query(type = Query.Type.INNER_LIKE)
    private ${column.columnType} ${column.changeColumnName};
</#if>
<#if column.queryType = '!='>

    @Query(type = Query.Type.NOT_EQUAL)
    private ${column.columnType} ${column.changeColumnName};
</#if>
<#if column.queryType = 'NotNull'>

    @Query(type = Query.Type.NOT_NULL)
    private ${column.columnType} ${column.changeColumnName};
</#if>
<#if column.queryType = '>='>

    @Query(type = Query.Type.GREATER_THAN)
    private ${column.columnType} ${column.changeColumnName};
</#if>
<#if column.queryType = '<='>

    @Query(type = Query.Type.LESS_THAN)
    private ${column.columnType} ${column.changeColumnName};
</#if>
    </#list>
</#if>
<#if betweens??>
    <#list betweens as column>
     /** BETWEEN */
     @Query(type = Query.Type.BETWEEN)
     private List<${column.columnType}> createTime;
    </#list>
</#if>
}
