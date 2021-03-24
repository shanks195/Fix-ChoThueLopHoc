package vn.thuephonghoc.query;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import vn.thuephonghoc.annotation.Query;

@Data
public class ContactClientQueryCriteria implements Serializable {

	 @Query
	 private Long id;
	 
	@Query(blurry = "fullname,email,phone,schedule")
	private String blurry;
	
   @Query(type = Query.Type.BETWEEN)
   private List<Timestamp> createTime;
}
