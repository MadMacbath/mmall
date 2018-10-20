package com.macbeth.autotask;

import com.macbeth.common.Constant;
import com.macbeth.common.RedissonManager;
import com.macbeth.service.OrderService;
import com.macbeth.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CloseOrderTask {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedissonManager redissonManager;

    @Scheduled(cron = "0 0 */1 * * ?")
    public void autoCloseOrderv2(){
        RLock lock = redissonManager.getRedisson().getLock(Constant.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean isLock = false;
        try {
            if (isLock = lock.tryLock(0,5, TimeUnit.SECONDS))
                orderService.closeOrder(Constant.CLOSE_ORDER_SCOPE);
        } catch (InterruptedException e) {
            log.info("获取锁失败",e);
        } finally {
            if (! isLock) return;
            lock.unlock();
        }

    }

//    @Scheduled(cron = "0 0 */1 * * ?")
//    public void autoCloseOrder(){
//        log.info("关单服务启动");
//        Long setnxResult = RedisUtils.setnx(Constant.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis() + Constant.LOCK_TIME));
//        if (setnxResult != null && setnxResult == 1l){
//            closeOrder();
//        } else {
//            Long result = Long.parseLong(RedisUtils.get(Constant.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK));
//            if (result != null && System.currentTimeMillis() > result){
//                String oldResult = RedisUtils.getset(Constant.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + Constant.LOCK_TIME));
//                if (oldResult == null || StringUtils.equals(result.toString(),oldResult)){
//                    closeOrder();
//                }
//            }
//        }
//        log.info("关单服务结束");
//    }
}
