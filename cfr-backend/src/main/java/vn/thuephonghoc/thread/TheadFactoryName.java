package vn.thuephonghoc.thread;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom thread name
 */
@Component
public class TheadFactoryName implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public TheadFactoryName() {
        this("cfrbackend-dinhducthien-pool");
    }

    private TheadFactoryName(String name){
        SecurityManager s = System.getSecurityManager();
        group = (s != null)? s.getThreadGroup():
                Thread.currentThread().getThreadGroup();
        //At this time, namePrefix is name + the first to create a thread pool with this factory
        this.namePrefix = name +
                POOL_NUMBER.getAndIncrement();
    }

    @Override
    public Thread newThread(Runnable r) {
        //The name of the thread at this time is namePrefix + -thread- + which thread of execution in this thread pool
        Thread t = new Thread(group, r,
                namePrefix + "-thread-"+threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
