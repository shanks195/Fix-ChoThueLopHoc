package vn.thuephonghoc.entity;

import lombok.*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "local_storage")
@NoArgsConstructor
public class LocalStorage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "name")
    private String name;

    @Column(name = "suffix")
    private String suffix;

    @Column(name = "path")
    private String path;


    @Column(name = "type")
    private String type;

 
    @Column(name = "size")
    private String size;

    @Column(name = "operate")
    private String operate;

    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    public LocalStorage(String realName, String name, String suffix, String path, String type, String size, String operate) {
        this.realName = realName;
        this.name = name;
        this.suffix = suffix;
        this.path = path;
        this.type = type;
        this.size = size;
        this.operate = operate;
    }

    public void copy(LocalStorage source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
