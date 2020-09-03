package com.vbrug.fw4j.core.util;

import com.vbrug.fw4j.core.util.algorithms.SnowFlake;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * A class to generate ID
 */
public abstract class IDUtil {

    private static long lastTimestamp = 0L;

    private static final SnowFlake SNOW_FLAKE = new SnowFlake(1,1);

    /**
     * Retrieve a UUID
     * @return A randomly generated UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 移除UUID的横线
     *
     * @return 
     */
    public static String randomUUIDRMLine() {
        return randomUUID().replaceAll("-", "");
    }

    /**
     * 获取毫秒级日期字符串
     * <p>保证唯一加锁，注意高并发效率</p>
     *
     * @return 返回毫秒级字符串
     */
    public static String getMillisDate() {
        long timestamp;
        synchronized (IDUtil.class) {
            timestamp = System.currentTimeMillis();
            while(lastTimestamp == timestamp) {
                try {
                    Thread.sleep(0, 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timestamp = System.currentTimeMillis();
            }
            lastTimestamp = timestamp;
        }
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(timestamp));
    }

    public static long nexId() {
        return SNOW_FLAKE.nextId();
    }

}
