package vn.thuephonghoc.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class DatabaseDto implements Serializable {


    private String id;

    private String name;

    private String jdbcUrl;

    private String pwd;

    private String userName;

    private Timestamp createTime;


}
