package vn.thuephonghoc.query;

import lombok.Data;
import vn.thuephonghoc.annotation.Query;

import java.sql.Timestamp;
import java.util.List;


@Data
public class DatabaseQueryCriteria{

    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query
    private String jdbcUrl;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
