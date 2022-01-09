package com.bitop.otcapi.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * RabbitmqConfig
 */
@Component
public class RabbitMQConfig {
    /**
     * 日志队列
     */
    public static final String EZCOINS_OPERLOG_QUEUE = "fanout_operlog_queue";
    /**
     * 唯一登录队列
     */
    public static final String EZCOINS_USERLOGIN_QUEUE = "fanout_userlogin_queue";
    /**
     * 配置fanout_log_queue
     *
     * @return
     */
    @Bean
    public Queue fanoutOperLogQueue() {
        return new Queue(EZCOINS_OPERLOG_QUEUE);
    }
    /**
     * 配置MAYIKT_UNIQUELOGIN_QUEUE
     *
     * @return
     */
    @Bean
    public Queue fanoutUserLoginQueue() {
        return new Queue(EZCOINS_USERLOGIN_QUEUE);
    }
    /**
     * 配置fanoutExchange
     *
     * @return
     */
    @Bean
    private FanoutExchange fanoutOperLogExchange() {
        return new FanoutExchange(EZCOINS_OPERLOG_QUEUE,false,true);
    }
    /**
     * 配置fanoutExchange
     *
     * @return
     */
    @Bean
    public FanoutExchange fanoutUserLoginExchange() {
        return new FanoutExchange(EZCOINS_USERLOGIN_QUEUE,false,true);
    }

    // 绑定交换机 Log
    @Bean
    public Binding bindingLogFanoutExchange(Queue fanoutOperLogQueue,FanoutExchange fanoutOperLogExchange) {
        return BindingBuilder.bind(fanoutOperLogQueue).to(fanoutOperLogExchange);
    }
    // 绑定交换机 fanoutUniqueloginQueue
    @Bean
    public Binding bindingUniqueLogFanoutExchange(Queue fanoutUserLoginQueue,FanoutExchange fanoutUserLoginExchange) {
        return BindingBuilder.bind(fanoutUserLoginQueue).to(fanoutUserLoginExchange);
    }

}
