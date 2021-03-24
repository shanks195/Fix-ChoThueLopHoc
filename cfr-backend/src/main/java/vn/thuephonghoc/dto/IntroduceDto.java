package vn.thuephonghoc.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class IntroduceDto implements Serializable{

	private Long id;
	private String name;
	private String title;
	private String link;
	private String description;
	private Timestamp createTime;
}
