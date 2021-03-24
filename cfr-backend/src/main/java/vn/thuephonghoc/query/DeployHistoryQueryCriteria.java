package vn.thuephonghoc.query;

import lombok.Data;
import vn.thuephonghoc.annotation.Query;

import java.sql.Timestamp;
import java.util.List;

@Data
public class DeployHistoryQueryCriteria{

 
    @Query(blurry = "appName,ip,deployUser")
    private String blurry;

    @Query
    private Long deployId;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> deployDate;

}
