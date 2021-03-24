package vn.thuephonghoc.query;

import lombok.Data;
import vn.thuephonghoc.annotation.Query;

@Data
public class DictQueryCriteria {

    @Query(blurry = "name,remark")
    private String blurry;
}

