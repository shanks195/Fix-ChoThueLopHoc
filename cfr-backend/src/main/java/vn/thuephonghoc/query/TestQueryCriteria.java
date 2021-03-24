package vn.thuephonghoc.query;

import lombok.Data;
import vn.thuephonghoc.annotation.Query;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TestQueryCriteria{

    @Query
    private Integer id;

    @Query
    private String email;

    @Query(type = Query.Type.INNER_LIKE)
    private String username;
     /** BETWEEN */
     @Query(type = Query.Type.BETWEEN)
     private List<Timestamp> createTime;
}
