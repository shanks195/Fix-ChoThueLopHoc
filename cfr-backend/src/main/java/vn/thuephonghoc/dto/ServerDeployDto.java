package vn.thuephonghoc.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class ServerDeployDto implements Serializable {

    private Long id;

    private String name;

    private String ip;

    private Integer port;

    private String account;

    private String password;

    private Timestamp createTime;
}
