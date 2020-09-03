package com.vbrug.fw4j.core.thread;

import com.vbrug.fw4j.core.util.HttpUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 并发线程测试
 */
public class ParallelThreadTest {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Object> map = new ConcurrentHashMap();
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch lock = new CountDownLatch(500);
        Runnable r = () -> {
            String result = "";
            String url = "http://39.98.169.101:8091/FightEvilForces/AITypeHomePageController/queryAiTypeSubjectStatList";
            String param = "{\"callTimeStart\":\"2019-01-01 00:00:00\",\"callTimeEnd\":\"2019-11-04 11:11:27\",\"isContainTrafficCase\":\"N\",\"xqdwCodes\":[]}";
//            String param = "\"jsonParam\" = \"{\"callTimeStart\":\"2019-01-01 00:00:00\",\"callTimeEnd\":\"2019-11-04 11:11:27\",\"isContainTrafficCase\":\"N\",\"xqdwCodes\":[]}\"";
            // String param = "jsonParam={}";
            try {
                latch.await();
                long start = System.currentTimeMillis();
                result = HttpUtil.doPost(url, param);
                map.put(Thread.currentThread().getName(), (System.currentTimeMillis()-start));
                lock.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(result.substring(0, 36));
        };
        for (int i =0; i< 500; i++)
            FixedThreadPool.getThreadPoolExecutor().submit(r);
        // System.out.println("---------sleep 10----------");
        Thread.sleep(3000);
        latch.countDown();
        lock.await();
        System.out.println(map);
        FixedThreadPool.shutdown();
    }
}
