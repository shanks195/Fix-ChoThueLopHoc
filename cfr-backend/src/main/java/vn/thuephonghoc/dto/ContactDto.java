package vn.thuephonghoc.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;


@Data
public class ContactDto implements Serializable {

    private Long id;
    
    private String title;
    
    private String description;
   
    private String email;
    
    private String phone;
    
    private Timestamp createTime;
}
