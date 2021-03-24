package vn.thuephonghoc.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class ContactClientDto implements Serializable {

    private Long id;
    
    private String fullname;
   
    private String email;
    
    private String phone;
    
    private String schedule;
}
