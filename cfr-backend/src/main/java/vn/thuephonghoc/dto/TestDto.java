package vn.thuephonghoc.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;



@Data
public class TestDto implements Serializable {
    // id
    private Integer id;

    private String email;

    private String username;

    private Timestamp createTime;
}
