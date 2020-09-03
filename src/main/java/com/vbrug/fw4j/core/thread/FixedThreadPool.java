package com.vbrug.fw4j.core.thread;

import java.lang.reflect.Method;
import java.util.concurrent.*;

public class FixedThreadPool {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
            300L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());

    public static Future submit(Object bean, String methodName, Object... args) throws Exception {
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        Method method = bean.getClass().getMethod(methodName, parameterTypes);
        Callable c = () -> {return method.invoke(bean, args);};
        return executor.submit(c);
    }

    public static ThreadPoolExecutor getThreadPoolExecutor(){
        return FixedThreadPool.executor;
    }

    public static void shutdown(){
        if (!executor.isShutdown())
            executor.shutdown();
    }
}
