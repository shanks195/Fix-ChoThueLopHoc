package vn.thuephonghoc.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;


@Getter
@Setter
public class LocalStorageDto implements Serializable {

    private Long id;

    private String realName;

    private String name;

    private String suffix;

    private String type;

    private String size;

    private String operate;

    private Timestamp createTime;

}
