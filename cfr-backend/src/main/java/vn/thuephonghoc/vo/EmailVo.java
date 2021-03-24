package vn.thuephonghoc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Class of receiving parameters when sending mail
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVo {

    // Recipients, support multiple recipients, separated by commas
    @NotEmpty
    private List<String> tos;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;
}

