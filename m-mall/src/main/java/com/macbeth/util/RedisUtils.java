package com.macbeth.util;

import com.macbeth.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisUtils {

    public static String set(String key, String value){
        String result = null;
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key : {} ,value : {}, error : {}",key,value,e);
            RedisShardedPool.returnBrokenSource(jedis);
            return result;
        }
        RedisShardedPool.returnSource(jedis);
        return result;
    }

    public static Long setnx(String key, String value){
        Long result = null;
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setnx(key,value);
        } catch (Exception e) {
            log.error("set key : {} ,value : {}, error : {}",key,value,e);
            RedisShardedPool.returnBrokenSource(jedis);
            return result;
        }
        RedisShardedPool.returnSource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e){
            log.error("get key : {} , error : {}",key,e);
            RedisShardedPool.returnBrokenSource(jedis);
            return result;
        }
        RedisShardedPool.returnSource(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e){
            log.error("del key : {} , error : {}",key,e);
            RedisShardedPool.returnBrokenSource(jedis);
            return result;
        }
        RedisShardedPool.returnSource(jedis);
        return result;
    }

    public static String setex(String key, String value,int exTime){
        String result = null;
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key : {} ,value : {}, error : {}",key,value,e);
            RedisShardedPool.returnBrokenSource(jedis);
            return result;
        }
        RedisShardedPool.returnSource(jedis);
        return result;
    }

    public static Long expire(String key,int exTime){
        Long result = null;
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key : {} ,exTime : {}, error : {}",key,exTime,e);
            RedisShardedPool.returnBrokenSource(jedis);
            return result;
        }
        RedisShardedPool.returnSource(jedis);
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        String value = get("name");
        System.out.println(value);
    }


    public static String getset(String closeOrderTaskLock, String valueOf) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.getSet(closeOrderTaskLock,valueOf);
        } catch (Exception e){
            log.error("get key : {} , error : {}",closeOrderTaskLock,e);
            RedisShardedPool.returnBrokenSource(jedis);
            return result;
        }
        RedisShardedPool.returnSource(jedis);
        return result;
    }
}
