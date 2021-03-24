package vn.thuephonghoc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "log")
@NoArgsConstructor
public class Log implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String description;

    private String method;

    @Column(columnDefinition = "text")
    private String params;

    @Column(name = "log_type")
    private String logType;

    @Column(name = "request_ip")
    private String requestIp;

    @Column(name = "address")
    private String address;

    private String browser;

    private Long time;

    @Column(name = "exception_detail", columnDefinition = "text")
    private byte[] exceptionDetail;

    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;

    public Log(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }
}

