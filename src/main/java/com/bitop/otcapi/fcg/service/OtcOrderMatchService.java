package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.response.Response;

public interface OtcOrderMatchService extends IService<OtcOrderMatch> {

    /***
     * @Description: 用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2022/01/04
     */
    Response cancelOrder(String matchOrderNo);

    /***
     * @Description: 买家确认 付款
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: taylor
     * @Date: 2022/01/04
     */
    Response confirmPayment(String matchOrderNo);

    /**
     * @Description: 卖家放款
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/19
     */
    Response sellerPut(String matchOrderNo,boolean isAdmin);


}
