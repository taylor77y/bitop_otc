package com.bitop.otcapi.mq.consumer;

import com.bitop.otcapi.fcg.entity.SysLog;
import com.bitop.otcapi.fcg.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 记录日志的消费者
 * @author taylor
 */
@Slf4j
@Component
@RabbitListener(queues = "fanout_operlog_queue")
public class OperLogConsumer {
    @Autowired
    private SysLogService sysLogService;

    @RabbitHandler
    public void process(SysLog sysLog) {
        try {
            sysLogService.save(sysLog);
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }
}