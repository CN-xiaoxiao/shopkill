package com.xiaoxiao.server.utils;

import java.util.concurrent.atomic.LongAdder;

/**
 * 请求id生成器
 * 雪花算法
 * 机房号：5bit
 * 机器号：5bit
 * 时间戳：42bit
 * 序列号：12bit
 * id: 时间戳 机房号 机器号 序列号
 */
public class IdGenerator {
    // 起始时间戳
    private static final long START_STAMP = DateUtil.get("2023-1-1").getTime();

    private static final long DATA_CENTER_BIT = 5L;
    private static final long MACHINE_BIT = 5L;
    private static final long SEQUENCE_BIT = 12L;

    // 最大值
    private static final long DATA_CENTER_MAX = ~(-1L << DATA_CENTER_BIT);
    private static final long MACHINE_MAX = ~(-1L << MACHINE_BIT);
    private static final long SEQUENCE_MAX = ~(-1L << SEQUENCE_BIT);

    private static final long TIMESTAMP_LEFT = DATA_CENTER_BIT + MACHINE_BIT + SEQUENCE_BIT;
    private static final long DATA_CENTER_LEFT = MACHINE_BIT + SEQUENCE_BIT;
    private static final long MACHINE_LEFT = SEQUENCE_BIT;

    private long dataCenterId;
    private long machineId;
    private LongAdder sequenceId = new LongAdder();
    private long lastTimeStamp = -1;

    private static IdGenerator ID_GENERATOR= new IdGenerator(2,3);

    private IdGenerator(long dataCenterId, long machineId) {
        if (dataCenterId > DATA_CENTER_MAX || machineId > MACHINE_MAX) {
            throw new IllegalArgumentException("传入的机房号或机器号不合法");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public static IdGenerator getIdGenerator() {
        return ID_GENERATOR;
    }

    public synchronized long getId() {
        long currentTime = System.currentTimeMillis();

        long timeStamp = currentTime - START_STAMP;

        if (timeStamp < lastTimeStamp) {
            throw new RuntimeException("服务器进行了时钟回拨");
        }

        if (timeStamp == lastTimeStamp) {
            sequenceId.increment();

            if (sequenceId.sum() >= SEQUENCE_MAX) {
                timeStamp = getNextTimeStamp();
                sequenceId.reset();
            }

        } else {
            sequenceId.reset();
        }

        lastTimeStamp = timeStamp;

        long sequence = sequenceId.sum();

        return timeStamp << TIMESTAMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private long getNextTimeStamp() {
        long current = System.currentTimeMillis() - START_STAMP;

        while (current == lastTimeStamp) {
            current = System.currentTimeMillis() - START_STAMP;
        }

        return current;
    }

//    public static void main(String[] args) {
//        IdGenerator idGenerator = new IdGenerator(1,2);
//        for (int i = 0; i < 10000; i++) {
//            new Thread(()-> System.out.println(idGenerator.getId())).start();
//        }
//    }
}