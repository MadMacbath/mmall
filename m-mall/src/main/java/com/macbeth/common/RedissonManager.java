package com.macbeth.common;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissonManager {
    private Config config = null;
    private Redisson redisson = null;

    @PostConstruct
    public void init(){
        config = new Config();
        config.useSingleServer().setAddress(Constant.REDIS_1_IP + ":" + Constant.REDIS_1_PORT);
        redisson = (Redisson) Redisson.create(config);
    }

    public Redisson getRedisson() {
        return redisson;
    }
}
