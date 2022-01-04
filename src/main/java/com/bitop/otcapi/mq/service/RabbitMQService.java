package com.bitop.otcapi.mq.service;

import com.bitop.otcapi.configuration.RabbitMQConfiguration;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void convert(String orderMatchNo,String status,Integer exTime){

        this.rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.orderExchange, //发送至订单交换机
                RabbitMQConfiguration.routingKeyOrder, //订单定routingKey
                orderMatchNo + "_" + status //订单号   这里可以传对象 比如直接传订单对象
                , message -> {
                    message.getMessageProperties().setExpiration(1000 * 60 * exTime +"");
                    return message;
                });
    }

}
