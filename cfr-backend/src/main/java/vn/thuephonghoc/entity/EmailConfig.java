package vn.thuephonghoc.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Mail configuration category, data storage overwrite storage in data storage
 */
@Entity
@Data
@Table(name = "email_config")
public class EmailConfig implements Serializable {

    @Id
    private Long id;

    @NotBlank
    private String host;

    @NotBlank
    private String port;

    @NotBlank
    private String user;

    @NotBlank
    private String pass;

    @NotBlank
    @Column(name = "from_user")
    private String fromUser;
}

