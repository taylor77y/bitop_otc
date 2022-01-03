package com.bitop.otcapi.manager.factory;

import com.bitop.otcapi.constant.SysTipsConstants;
import com.bitop.otcapi.fcg.entity.SysTips;
import com.bitop.otcapi.fcg.service.SysTipsService;
import com.bitop.otcapi.util.SpringUtils;

import java.util.TimerTask;

public class AsyncFactory {

    /**
     * 站内信
     */
    public static TimerTask StationLetter(String userId, SysTipsConstants.TipsType tipsType, Object ... args){
        SysTips ezSysTips=new SysTips();
        ezSysTips.setUserId(userId);
        ezSysTips.setTitle("系统信息");
        ezSysTips.setContent(String.format(tipsType.getRemark(),args));
        return new TimerTask(){
            @Override
            public void run(){
                SpringUtils.getBean(SysTipsService.class).save(ezSysTips);
            }
        };
    }
}
