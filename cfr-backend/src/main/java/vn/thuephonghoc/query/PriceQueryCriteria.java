package vn.thuephonghoc.query;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import vn.thuephonghoc.annotation.Query;

@Data
public class PriceQueryCriteria {

	@Query
	private Long id;
	 
	@Query(blurry = "title,description,name,link")
	private String blurry;
	
	@Query(type = Query.Type.BETWEEN)
	private List<Timestamp> createTime;
}
