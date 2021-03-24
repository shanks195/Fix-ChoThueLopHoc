package vn.thuephonghoc.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "quartz_log")
public class QuartzLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 // mission name
    @Column(name = "job_name")
    private String jobName;

    // Bean name
    @Column(name = "baen_name")
    private String beanName;

    // method name
    @Column(name = "method_name")
    private String methodName;

    // parameters
    @Column(name = "params")
    private String params;

    // cron expression
    @Column(name = "cron_expression")
    private String cronExpression;

    // status
    @Column(name = "is_success")
    private Boolean isSuccess;

    // exception details
    @Column(name = "exception_detail",columnDefinition = "text")
    private String exceptionDetail;

    // Time-consuming (milliseconds)
    private Long time;

    // Creation date
    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;
}

