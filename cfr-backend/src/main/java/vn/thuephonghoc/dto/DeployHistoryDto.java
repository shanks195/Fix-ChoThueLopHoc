package vn.thuephonghoc.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;


@Data
public class DeployHistoryDto implements Serializable {


    private String id;

    private String appName;

    private String ip;

    private Timestamp deployDate;

    private String deployUser;

    private Long deployId;
}
