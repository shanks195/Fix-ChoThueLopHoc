package vn.thuephonghoc.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import vn.thuephonghoc.utils.SpringContextHolder;

/**
 * Used to get a custom thread pool
 */
public class ThreadPoolExecutorUtil {

    public static ThreadPoolExecutor getPoll(){
        AsyncTaskProperties properties = SpringContextHolder.getBean(AsyncTaskProperties.class);
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(properties.getQueueCapacity()),
                new TheadFactoryName()
        );
    }
}
