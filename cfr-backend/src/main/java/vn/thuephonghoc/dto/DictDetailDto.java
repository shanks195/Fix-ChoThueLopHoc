package vn.thuephonghoc.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;


@Getter
@Setter
public class DictDetailDto implements Serializable {
	
    private Long id;

    private String label;

    private String value;

    private Integer sort;

    private DictSmallDto dict;

    private Timestamp createTime;
}
