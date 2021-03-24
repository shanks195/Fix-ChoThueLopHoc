package vn.thuephonghoc.dto;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;


@Data
public class DeployDto implements Serializable {


    private String id;

    private AppDto app;

    private Set<ServerDeployDto> deploys;

    private String servers;

    private String status;

    private Timestamp createTime;

    public String getServers() {
        if(CollectionUtil.isNotEmpty(deploys)){
            return deploys.stream().map(ServerDeployDto::getName).collect(Collectors.joining(","));
        }
        return servers;
    }

}
