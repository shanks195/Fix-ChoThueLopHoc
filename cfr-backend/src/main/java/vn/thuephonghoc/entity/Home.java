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
@Table(name = "home")
@NoArgsConstructor
public class Home implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String link;
    
    @NotBlank
    private String description;
    
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;
	


    public void copy(Home resources) {
        BeanUtil.copyProperties(resources, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
