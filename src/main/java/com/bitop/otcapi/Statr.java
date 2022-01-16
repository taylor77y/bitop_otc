package com.bitop.otcapi;

import com.bitop.otcapi.handler.AuthenticationInterceptor;
import com.bitop.otcapi.scheduledtasks.ScheduledTasks;
import com.bitop.otcapi.util.MoneyChangeUtils;
import com.bitop.otcapi.websocket.ToUsdtPrice;
import com.bitop.otcapi.websocket.WebSocketHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: taylor_fcg
 * @Email: taylor77y@gmail.com
 * @Description:
 * @Date:2021/7/6 14:56
 * @Version:1.0
 */
@Order(1)
@Component
@Slf4j
public class Statr implements CommandLineRunner {

    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void run(String... args) throws Exception {
        AuthenticationInterceptor.flag = false;
        sync();
        AuthenticationInterceptor.flag = true;
    }
    private void sync() throws Exception {
        ScheduledTasks.rmbPrice = MoneyChangeUtils.getUSDToCNY();
        if (ScheduledTasks.rmbPrice!=null){
            log.info("启动成功");
        }
 /*       taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        List<ToUsdtPrice> list = ScheduledTasks.getPrice();
                        ToUsdtPrice toUsdtPrice = new ToUsdtPrice();
                        toUsdtPrice.setPrice(ScheduledTasks.rmbPrice);
                        toUsdtPrice.setName("usdt");
                        list.add(toUsdtPrice);
                        WebSocketHandle.price(list);
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
    }
}