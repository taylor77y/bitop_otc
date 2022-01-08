package com.bitop.otcapi.mq.consumer;

import com.bitop.otcapi.fcg.entity.SysLogininfor;
import com.bitop.otcapi.fcg.service.SysLogininforService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = "fanout_userlogin_queue")
public class UserLoginConsumer {

    @Autowired
    private SysLogininforService sysLogininforService;

    @RabbitHandler
    public void process(SysLogininfor sysLogininfor) {
        try {
            sysLogininforService.save(sysLogininfor);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
