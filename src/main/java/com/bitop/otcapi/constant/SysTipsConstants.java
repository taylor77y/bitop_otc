package com.bitop.otcapi.constant;

public class SysTipsConstants {

    public static enum TipsType {
        /**
         * 下架提醒
         **/
        OFF_SHELF("1", Tips.OFF_SHELF),
        /**
         * 系统下架
         **/
        SYS_OFF_SHELF("2", Tips.SYS_OFF_SHELF),
        /**
         * 上架提醒
         **/
        PUT_ON("3", Tips.PUT_ON),
        /**
         * 订单提示
         **/
        NEW_ORDER("4", Tips.NEW_ORDER),

        APPEALING("5", Tips.APPEALING),

        APPEAL_OFF("6", Tips.APPEAL_OFF),
        /**
         * 系统买单下架
         **/
        SYS_OFF_SHELF_BY("7", Tips.SYS_OFF_SHELF_BY),
        /**
         * 一键卖币失败
         */
        SELL_FAIL("8", Tips.SELL_FAIL),
        /**
         * 一键卖币成功
         */
        SELL_SUCCESS("9", Tips.SELL_SUCCESS),

        //        LOGINLIMIT("10", Tips.LOGINLIMIT),
        LOGINLIMIT_OFF("11", Tips.LOGINLIMIT_OFF),

        WITHDRAWLIMIT("13", Tips.WITHDRAWLIMIT),
        WITHDRAWLIMIT_OFF("14", Tips.WITHDRAWLIMIT_OFF),

        ORDERLIMIT("15", Tips.ORDERLIMIT),
        ORDERLIMIT_OFF("16", Tips.ORDERLIMIT_OFF),


        BUSINESSLIMIT("17", Tips.BUSINESSLIMIT),
        BUSINESSLIMIT_OFF("18", Tips.BUSINESSLIMIT_OFF);

        private String type;

        private String remark;

        private TipsType(String type, String remark) {
            this.type = type;
            this.remark = remark;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

    }

    public static interface Tips {

        /**
         * 下架提醒
         */
        String OFF_SHELF = "订单号：%s 下架成功 ，冻结金额 %s 已返回你的可用余额,请查收";
        /**
         * 系统下架
         */
        String SYS_OFF_SHELF = "您的订单号：%s 剩余出售数量已不满足最低限额，系统已自动下架该订单! 冻结金额 %s 已返回你的可用余额,请查收";
        /**
         * 上架提醒
         */
        String PUT_ON = "订单发布成功，订单号为：%s";

        /**
         * 订单提示
         */
        String NEW_ORDER = "你有新的待处理订单，请前往处理！";

        /**
         * 申诉提示
         */
        String APPEALING = "订单号：%s 接到商户：%s 申诉,请尽快联系对方进行处理！";


        String APPEAL_OFF = "订单号：%s , 申诉状态以改变,请尽快联系对方进行处理！";


        String SYS_OFF_SHELF_BY = "您的订单号：%s 剩余购买数量已不满足最低限额，系统已自动下架该订单!";

        String SELL_FAIL = "一键卖币订单：%s 系统已拒绝您的请求，冻结金额 %s 已返还回账户，请注意查收！如有疑问，请联系客服进行确认后，进行操作!";

        String SELL_SUCCESS = "一键卖币订单：%s，已成功卖出 %s ，卖出总金额: %s 将稍后打入你指定银行账户，请注意查收，如有疑问，请联系客服!";

        String WITHDRAWLIMIT = "当前用户已被限制提币，主要原因：%S,封禁时间：%s 天，如需申诉，请联系客服!";
        String WITHDRAWLIMIT_OFF = "当前用户提币已被解禁,如有疑问，请联系客服!";

        String ORDERLIMIT = "当前用户已被限制发布广告，主要原因：%S,封禁时间：%s 天 ，如需申诉，请联系客服!";
        String ORDERLIMIT_OFF = "当前用户发布广告已被解禁,如有疑问，请联系客服!";

        String BUSINESSLIMIT = "当前用户已被限制买卖，主要原因：%S,封禁时间：%s 天，如需申诉，请联系客服!";
        String BUSINESSLIMIT_OFF = "当前用户买卖已被解禁,如有疑问，请联系客服!";

        String LOGINLIMIT_OFF = "当前用户登录已被解禁,如有疑问，请联系客服!";
    }


}
