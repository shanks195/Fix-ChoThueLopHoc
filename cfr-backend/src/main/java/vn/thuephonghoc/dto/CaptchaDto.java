package vn.thuephonghoc.dto;

import lombok.Data;
import java.io.Serializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;



@Data
public class CaptchaDto implements Serializable {
    // id
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;
    
    private Integer type;
    
    private String fontName;
    
    private Integer fontStyle;
    
    private Integer fontSize;

    private Integer width;

    private Integer height;

    private Integer len;
}
