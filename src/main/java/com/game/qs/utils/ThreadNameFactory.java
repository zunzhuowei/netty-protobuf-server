package com.game.qs.utils;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程名字工厂
 */
public class ThreadNameFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private final String namePrefix;
    private final boolean daemon;

    public ThreadNameFactory(String namePreFix) {
        this(namePreFix, false);
    }

    public ThreadNameFactory(String namePreFix, boolean daemon) {
        SecurityManager securityManager = System.getSecurityManager();
        group = Objects.nonNull(securityManager)
                ? securityManager.getThreadGroup()
                : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePreFix + "-thread-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix
                + threadNumber.getAndIncrement(), 0);
        if (daemon) {
            t.setDaemon(daemon);
        } else {
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
        }
        return t;
    }

    public String getNamePrefix() {
        return namePrefix;
    }
}
