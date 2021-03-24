package vn.thuephonghoc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Table data information
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {

    // table name
    private Object tableName;

    // Creation date
    private Object createTime;

    // database engine
    private Object engine;

    // code set
    private Object coding;

    // Remarks
    private Object remark;

}
