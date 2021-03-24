package vn.thuephonghoc.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *pv and ip statistics
 */
@Entity
@Data
@Table(name = "visits")
public class Visits implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String date;

    @Column(name = "pv_counts")
    private Long pvCounts;

    @Column(name = "ip_counts")
    private Long ipCounts;

    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "week_day")
    private String weekDay;
}

