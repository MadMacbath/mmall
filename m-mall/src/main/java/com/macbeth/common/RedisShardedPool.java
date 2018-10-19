package com.macbeth.common;


import com.google.common.collect.Lists;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.List;

public class RedisShardedPool {
    public static ShardedJedisPool pool;

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

        List<JedisShardInfo> jedisShardInfos = Lists.newArrayList();
        JedisShardInfo jedisShardInfo_1 = new JedisShardInfo(Constant.REDIS_1_IP,Constant.REDIS_1_PORT,Constant.REDIS_TIMEOUT);
        jedisShardInfo_1.setPassword(Constant.REDIS_1_PASSWORD);

        JedisShardInfo jedisShardInfo_2 = new JedisShardInfo(Constant.REDIS_2_IP,Constant.REDIS_2_PORT,Constant.REDIS_TIMEOUT);
        jedisShardInfo_2.setPassword(Constant.REDIS_2_PASSWORD);

        jedisShardInfos.add(jedisShardInfo_1);
        jedisShardInfos.add(jedisShardInfo_2);

        pool = new ShardedJedisPool(config,jedisShardInfos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenSource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void returnSource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = getJedis();

        for (int i = 0; i < 10; i++){
            String value = jedis.get("key " + i);
            System.out.println(value);
        }
        returnSource(jedis);

        pool.destroy();
    }
}
