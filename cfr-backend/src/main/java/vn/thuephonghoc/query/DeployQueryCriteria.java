package vn.thuephonghoc.query;

import lombok.Data;
import vn.thuephonghoc.annotation.Query;

import java.sql.Timestamp;
import java.util.List;

@Data
public class DeployQueryCriteria{

    @Query(type = Query.Type.INNER_LIKE, propName = "name", joinName = "app")
    private String appName;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;

}
