package vn.thuephonghoc.entity;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name="mnt_database")
public class Database implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "jdbc_url",nullable = false)
    private String jdbcUrl;

    @Column(name = "pwd",nullable = false)
    private String pwd;

    @Column(name = "user_name",nullable = false)
    private String userName;

    @CreationTimestamp
    private Timestamp createTime;

    public void copy(Database source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
