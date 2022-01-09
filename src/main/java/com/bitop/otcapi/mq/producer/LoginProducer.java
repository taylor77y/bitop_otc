package com.bitop.otcapi.mq.producer;

import com.bitop.otcapi.configuration.RabbitMQConfig;
import com.bitop.otcapi.fcg.entity.SysLogininfor;
import com.bitop.otcapi.util.AddressUtils;
import com.bitop.otcapi.util.IpUtils;
import com.bitop.otcapi.util.LogUtils;
import com.bitop.otcapi.util.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class LoginProducer {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 登录之后 后续异步处理的代码
     */

    public void sendMsgLoginFollowUp(String userName,String userId , String phone,String userType) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        String address = AddressUtils.getRealAddressByIP(ip);
        String os = userAgent.getOperatingSystem().getName();
        String browser = userAgent.getBrowser().getName();

        StringBuilder s = new StringBuilder();
        s.append(LogUtils.getBlock(ip));
        s.append(LogUtils.getBlock(address));
        s.append(LogUtils.getBlock(userName));
        s.append(LogUtils.getBlock(userType));
        s.append(LogUtils.getBlock(phone));
        log.info(s.toString());

        SysLogininfor ezSysLogininfor = new SysLogininfor();
        ezSysLogininfor.setLoginLocation(address);
        ezSysLogininfor.setBrowser(os);
        ezSysLogininfor.setUserId(userId);
        ezSysLogininfor.setIpaddr(ip);
        ezSysLogininfor.setOs(os);
        ezSysLogininfor.setUserName(userName);
        ezSysLogininfor.setUserType(userType);
        ezSysLogininfor.setBrowser(browser);
        ezSysLogininfor.setCreateTime(new Date());
        // 使用rabbitmq投递消息
        amqpTemplate.convertAndSend(RabbitMQConfig.EZCOINS_USERLOGIN_QUEUE, "", ezSysLogininfor);
        log.info(">>>会员服务登录后续，投递消息到mq成功.<<<");
    }
}
