package vn.thuephonghoc.query;

import lombok.Data;
import vn.thuephonghoc.annotation.Query;

import java.sql.Timestamp;
import java.util.List;

@Data
public class MenuQueryCriteria {

    @Query(blurry = "name,path,component")
    private String blurry;


    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
