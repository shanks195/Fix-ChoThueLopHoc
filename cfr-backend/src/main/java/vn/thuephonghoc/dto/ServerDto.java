package vn.thuephonghoc.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServerDto implements Serializable {

    private Integer id;

    private String name;

    private String address;

    private Integer port;

    private String state;

    private Float cpuRate;

    private Integer cpuCore;

    private Float memTotal;

    private Float memUsed;

    private Float diskTotal;

    private Float diskUsed;

    private Float swapTotal;

    private Float swapUsed;

    private Integer sort;
}
