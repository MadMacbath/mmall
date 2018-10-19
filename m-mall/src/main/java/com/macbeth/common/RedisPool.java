package com.macbeth.common;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    public static JedisPool pool;

    static{
        initPool();
    }

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(Constant.REDIS_MAX_TOTAL);
        config.setMaxIdle(Constant.REDIS_MAX_IDLE);
        config.setMinIdle(Constant.REDIS_MIN_IDLE);
        config.setTestOnBorrow(Constant.REDIS_TEST_ON_BORROW);
        config.setTestOnReturn(Constant.REDIS_TEST_ON_RETURN);
        config.setBlockWhenExhausted(true);

        System.out.println(Constant.REDIS_1_IP);
        pool = new JedisPool(config,Constant.REDIS_1_IP,Constant.REDIS_1_PORT, Constant.REDIS_TIMEOUT, Constant.REDIS_1_PASSWORD);
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenSource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void returnSource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = getJedis();
        jedis.select(15);
        jedis.set("name","macbeth");
        returnSource(jedis);
        pool.destroy();
    }
}
