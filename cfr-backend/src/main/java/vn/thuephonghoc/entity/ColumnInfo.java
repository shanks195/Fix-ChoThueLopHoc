package vn.thuephonghoc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.thuephonghoc.utils.GenUtil;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@Table(name = "column_config")
public class ColumnInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String tableName;

    private String columnName;

    private String columnType;

    private String keyType;

    private String extra;

    private String remark;

    private Boolean notNull;

    private Boolean listShow;

    private Boolean formShow;

    private String formType;

    private String queryType;

    private String dictName;

    private String dateAnnotation;


    public ColumnInfo(String tableName, String columnName, Boolean notNull, String columnType, String remark, String keyType, String extra) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnType = columnType;
        this.keyType = keyType;
        this.extra = extra;
        this.notNull = notNull;
        if(GenUtil.PK.equalsIgnoreCase(keyType) && GenUtil.EXTRA.equalsIgnoreCase(extra)){
            this.notNull = false;
        }
        this.listShow = true;
        this.formShow = true;
    }

}
