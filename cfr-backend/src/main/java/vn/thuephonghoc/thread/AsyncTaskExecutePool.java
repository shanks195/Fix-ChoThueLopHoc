package vn.thuephonghoc.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Asynchronous task thread pool assembly class
 */
@Slf4j
@Configuration
public class AsyncTaskExecutePool implements AsyncConfigurer {

    //Inject configuration class
    private final AsyncTaskProperties config;

    public AsyncTaskExecutePool(AsyncTaskProperties config) {
        this.config = config;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //Core thread pool size
        executor.setCorePoolSize(config.getCorePoolSize());
        //Maximum number of threads
        executor.setMaxPoolSize(config.getMaxPoolSize());
        //Queue capacity
        executor.setQueueCapacity(config.getQueueCapacity());
        //Active time
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        //Thread name prefix
        executor.setThreadNamePrefix("cfrbackend-dinhducthien-async-");
        // setRejectedExecutionHandler: How to handle new tasks when the pool has reached max size
        // CallerRunsPolicy: do not perform tasks in a new thread, but by the thread where the caller is located
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("===="+throwable.getMessage()+"====", throwable);
            log.error("Exception method:"+method.getName());
        };
    }

}
