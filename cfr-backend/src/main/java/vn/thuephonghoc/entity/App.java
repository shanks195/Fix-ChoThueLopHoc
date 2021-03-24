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
@Table(name="mnt_app")
public class App implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "port")
    private int port;
    
    @Column(name = "upload_path")
    private String uploadPath;

    @Column(name = "deploy_path")
    private String deployPath;

    @Column(name = "backup_path")
    private String backupPath;

    @Column(name = "start_script")
    private String startScript;

    @Column(name = "deploy_script")
    private String deployScript;

    @CreationTimestamp
    private Timestamp createTime;

    public void copy(App source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
