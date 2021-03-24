package vn.thuephonghoc.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;


@Data
public class AppDto implements Serializable {


    private Long id;

    private String name;

    private Integer port;

    private String uploadPath;

    private String deployPath;

    private String backupPath;

    private String startScript;

    private String deployScript;

    private Timestamp createTime;

}
