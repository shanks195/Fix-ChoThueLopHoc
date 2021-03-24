package vn.thuephonghoc.entity;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.io.Serializable;

@Entity
@Data
@Table(name="test")
public class Test implements Serializable {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email",nullable = false)
    @NotBlank
    private String email;

    @Column(name = "username",nullable = false)
    @NotBlank
    private String username;

    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    public void copy(Test source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
