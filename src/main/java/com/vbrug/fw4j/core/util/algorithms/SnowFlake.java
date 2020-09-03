package com.vbrug.fw4j.core.util.algorithms;

public class SnowFlake {

    private final static long SEQUENCE_BIT = 12;
    private final static long MACHINE_BIT = 5;
    private final static long DATACENTER_BIT = 5;

    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId;
    private long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowFlake(long datacenterId, long machineId){
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0){
            throw new IllegalArgumentException(
                    "datacenterId can't be greater than"
                            + datacenterId + " or less than 0");
        }

        if (machineId > MAX_MACHINE_NUM || machineId < 0){
            throw new IllegalArgumentException("machineId can't be greater than "
                    + MAX_MACHINE_NUM + " or less than 0 ");
        }

        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId(){
        long currTimestamp = System.currentTimeMillis();
        if (currTimestamp < lastTimestamp){
            throw new RuntimeException("Clock moved backwards. refusing to genetate id");
        }

        if (currTimestamp == lastTimestamp){
            sequence = (sequence +1) & MAX_SEQUENCE;
            if (sequence == 0L)
                currTimestamp = getNexMill();

        } else {
            sequence = 0L;
        }
        lastTimestamp = currTimestamp;
        return currTimestamp << TIMESTAMP_LEFT
                | datacenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    public long nextId2(){
        long currTimestamp;
        synchronized (this) {
            currTimestamp = System.currentTimeMillis();
            if (currTimestamp < lastTimestamp) {
                throw new RuntimeException("Clock moved backwards. refusing to genetate id");
            }

            if (currTimestamp == lastTimestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                if (sequence == 0L)
                    currTimestamp = getNexMill();

            } else {
                sequence = 0L;
            }
            lastTimestamp = currTimestamp;
        }
        return currTimestamp << TIMESTAMP_LEFT
                | datacenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private long getNexMill(){
        long mill = System.currentTimeMillis();
        while (mill <= lastTimestamp)
            mill = System.currentTimeMillis();
        return mill;
    }

    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(1, 1);
        long      startTime = System.currentTimeMillis();
        for (int i = 0; i< 1000000; i++){
            System.out.println(snowFlake.nextId2());
        }
        long lastTimeConsume = System.currentTimeMillis()-startTime;

        startTime = System.currentTimeMillis();
        for (int i = 0; i< 1000000; i++){
            System.out.println(snowFlake.nextId());
        }
        System.out.println("lastConsumeTime: "+ lastTimeConsume +  "  consumeTime: "+(System.currentTimeMillis()-startTime));
    }

}
