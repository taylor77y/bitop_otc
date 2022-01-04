package com.bitop.otcapi.constant;

public class SysOrderConstants{

    public static enum SysChatMsg{
        /** 下单(买单)聊天消息 **/
        BUY_PLACE_ORDER(SystemOrderTips.PLACE_ORDER_SUCCESS,SystemOrderTips.ORDER_SUCCESS),
        /** 卖单(买单)聊天消息 **/
        SELL_PLACE_ORDER(SystemOrderTips.PLACE_ORDER_SUCCESS_SELL,SystemOrderTips.ORDER_SUCCESS_SELL),
        /** 支付成功聊天消息 **/
        PAYMENT_SUCCESS(SystemOrderTips.PAYMENT_SELL,SystemOrderTips.PAYMENT_BUY),
        /** 成功(买单)聊天消息 **/
        RELEASE_SUCCESS(SystemOrderTips.RELEASE_SELL,SystemOrderTips.RELEASE_BUY),

        /**申诉提示**/
        APPEALING(SystemOrderTips.APPEALING_ID,SystemOrderTips.APPEALING_TO_ID),

        /**申诉取消状态提示**/
        APPEAL_OFF(SystemOrderTips.APPEAL_OFF,SystemOrderTips.APPEAL_OFF),

        /**申诉放行**/
        APPEAL_PUT(SystemOrderTips.APPEAL_PUT,SystemOrderTips.APPEAL_PUT),

        /**审核结果为取消订单**/
        APPEAL_CANCEL(SystemOrderTips.APPEAL_CANCEL,SystemOrderTips.APPEAL_CANCEL),

        /** 取消订单 **/
        CANCEL_SUCCESS(SystemOrderTips.CANCEL,SystemOrderTips.CANCEL_2);

        private String sellTips;

        private String buyTips;

        SysChatMsg(String sellTips, String buyTips) {
            this.sellTips = sellTips;
            this.buyTips = buyTips;
        }

        public String getSellTips() {
            return sellTips;
        }

        public void setSellTips(String sellTips) {
            this.sellTips = sellTips;
        }

        public String getBuyTips() {
            return buyTips;
        }
        public void setBuyTips(String buyTips) {
            this.buyTips = buyTips;
        }

    }
}
