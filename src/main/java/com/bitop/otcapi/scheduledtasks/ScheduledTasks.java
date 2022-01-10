package com.bitop.otcapi.scheduledtasks;

import com.bitop.otcapi.util.EthUsdtBtcUtils;
import com.bitop.otcapi.util.MoneyChangeUtils;
import com.bitop.otcapi.websocket.ToUsdtPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
@Configurable
//@EnableScheduling
@Slf4j
public class ScheduledTasks {

    public volatile static BigDecimal rmbPrice = BigDecimal.ZERO;
    public volatile static BigDecimal btcusdt = BigDecimal.ZERO;
    public volatile static BigDecimal ethusdt = BigDecimal.ZERO;
    public volatile static BigDecimal bchusdt = BigDecimal.ZERO;
    public volatile static BigDecimal dogeusdt = BigDecimal.ZERO;
    public volatile static BigDecimal adausdt = BigDecimal.ZERO;
    public volatile static BigDecimal xrpusdt = BigDecimal.ZERO;
    public volatile static BigDecimal filusdt = BigDecimal.ZERO;
    public volatile static BigDecimal trxusdt = BigDecimal.ZERO;
    public volatile static BigDecimal ltcusdt = BigDecimal.ZERO;
    /**
     * 每隔半小时
     **/
    @Scheduled(cron = "${scheduled.cron.rmbPrice}")
    public void upHotelStar() {
        BigDecimal usdToCNY = MoneyChangeUtils.getUSDToCNY();
        if (null != usdToCNY) {
            rmbPrice = usdToCNY;
            log.info("获取rmb - u 价格成功 ！！！--->{}", rmbPrice);
        } else {
            log.info("获取rmb - u 失败 ！！！--->{}", rmbPrice);
        }
    }
    public static List<ToUsdtPrice> getPrice() {
        btcusdt = EthUsdtBtcUtils.getSymbol("btcusdt");
        ToUsdtPrice toUsdtPrice = new ToUsdtPrice();
        toUsdtPrice.setPrice(btcusdt);
        toUsdtPrice.setName("btc");
        ethusdt = EthUsdtBtcUtils.getSymbol("ethusdt");
        ToUsdtPrice toUsdtPrice1 = new ToUsdtPrice();
        toUsdtPrice1.setPrice(ethusdt);
        toUsdtPrice1.setName("eth");
        bchusdt = EthUsdtBtcUtils.getSymbol("bchusdt");
        ToUsdtPrice toUsdtPrice2 = new ToUsdtPrice();
        toUsdtPrice2.setPrice(bchusdt);
        toUsdtPrice2.setName("bch");
        dogeusdt = EthUsdtBtcUtils.getSymbol("dogeusdt");
        ToUsdtPrice toUsdtPrice3 = new ToUsdtPrice();
        toUsdtPrice3.setPrice(dogeusdt);
        toUsdtPrice3.setName("doge");
        adausdt = EthUsdtBtcUtils.getSymbol("adausdt");
        ToUsdtPrice toUsdtPrice4 = new ToUsdtPrice();
        toUsdtPrice4.setPrice(adausdt);
        toUsdtPrice4.setName("ada");
        xrpusdt = EthUsdtBtcUtils.getSymbol("xrpusdt");
        ToUsdtPrice toUsdtPrice5 = new ToUsdtPrice();
        toUsdtPrice5.setPrice(xrpusdt);
        toUsdtPrice5.setName("xrp");
        filusdt = EthUsdtBtcUtils.getSymbol("filusdt");
        ToUsdtPrice toUsdtPrice6 = new ToUsdtPrice();
        toUsdtPrice6.setPrice(filusdt);
        toUsdtPrice6.setName("fil");
        trxusdt = EthUsdtBtcUtils.getSymbol("trxusdt");
        ToUsdtPrice toUsdtPrice7 = new ToUsdtPrice();
        toUsdtPrice7.setPrice(trxusdt);
        toUsdtPrice7.setName("trx");
        ltcusdt = EthUsdtBtcUtils.getSymbol("ltcusdt");
        ToUsdtPrice toUsdtPrice8 = new ToUsdtPrice();
        toUsdtPrice8.setPrice(ltcusdt);
        toUsdtPrice8.setName("ltc");
        List<ToUsdtPrice> list=new ArrayList<>();
        list.add(toUsdtPrice);
        list.add(toUsdtPrice1);
        list.add(toUsdtPrice2);
        list.add(toUsdtPrice3);
        list.add(toUsdtPrice4);
        list.add(toUsdtPrice5);
        list.add(toUsdtPrice6);
        list.add(toUsdtPrice7);
        list.add(toUsdtPrice8);
        return list;
    }

}