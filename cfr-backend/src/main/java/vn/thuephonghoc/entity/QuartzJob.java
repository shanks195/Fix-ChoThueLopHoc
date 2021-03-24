package vn.thuephonghoc.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "quartz_job")
public class QuartzJob implements Serializable {

    public static final String JOB_KEY = "JOB_KEY";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(groups = {Update.class})
    private Long id;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "bean_name")
    @NotBlank
    private String beanName;

    @Column(name = "method_name")
    @NotBlank
    private String methodName;

    @Column(name = "params")
    private String params;

    @Column(name = "cron_expression")
    @NotBlank
    private String cronExpression;

    @Column(name = "is_pause")
    private Boolean isPause = false;

    @Column(name = "remark")
    @NotBlank
    private String remark;

    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    public @interface Update {}

}
