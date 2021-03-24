package vn.thuephonghoc.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name="dict_detail")
public class DictDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull(groups = Update.class)
    private Long id;

    @Column(name = "label",nullable = false)
    private String label;


    @Column(name = "value",nullable = false)
    private String value;

    @Column(name = "sort")
    private Integer sort = 999;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "dict_id")
    private Dict dict;

    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    public @interface Update {}

}
