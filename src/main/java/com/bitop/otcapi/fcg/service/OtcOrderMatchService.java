package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.req.AdMatchOrderQueryReqDto;
import com.bitop.otcapi.fcg.entity.resp.OrderRecordRespDto;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.response.ResponseList;

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
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2022/01/04
     */
    Response confirmPayment(String matchOrderNo);

    /**
     * @Description: 卖家放款
     * @Param: [matchOrderNo]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2022/01/04
     */
    Response sellerPut(String matchOrderNo,boolean isAdmin);


    /**
     * 付款失败  后台修改订单为取消
     */
    void paymentFail(String matchOrderNo);

    /***
     * @Description: 广告订单匹配订单
     * @Param: [matchOrderQueryReqDto]
     * @return: com.ezcoins.response.ResponseList<com.ezcoins.project.otc.entity.resp.OrderRecordRespDto>
     * @Author: taylor
     * @Date: 2022/01/18
     */
    ResponseList<OrderRecordRespDto> adMatchOrder(AdMatchOrderQueryReqDto matchOrderQueryReqDto);
}
