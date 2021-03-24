package vn.thuephonghoc.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verification_code")
public class VerificationCode implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

 // Use scenario, define by yourself
    private String scenes;

    // true is valid, false is invalid, status + time + specific email or phone number during verification
    private Boolean status = true;


    // Type: phone and email
    @NotBlank
    private String type;

    // specific phone and email
    @NotBlank
    private String value;


    public VerificationCode(String code, String scenes, @NotBlank String type, @NotBlank String value) {
        this.code = code;
        this.scenes = scenes;
        this.type = type;
        this.value = value;
    }
}
