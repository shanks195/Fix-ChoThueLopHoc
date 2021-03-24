package vn.thuephonghoc.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "contact")
@NoArgsConstructor
public class Contact implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    private String title;
    
    private String description;
    
    @Column(name = "email",nullable = false)
    @NotBlank
    private String email;
    
    private String phone;
    
    
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;
    
    public void copy(Contact source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

}
