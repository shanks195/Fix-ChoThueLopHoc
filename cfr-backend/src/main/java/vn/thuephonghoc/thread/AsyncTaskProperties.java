package vn.thuephonghoc.thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Thread pool configuration attribute class
 */
@Data
@Component
@ConfigurationProperties(prefix = "task.pool")
public class AsyncTaskProperties {

    private int corePoolSize;

    private int maxPoolSize;

    private int keepAliveSeconds;

    private int queueCapacity;
}
