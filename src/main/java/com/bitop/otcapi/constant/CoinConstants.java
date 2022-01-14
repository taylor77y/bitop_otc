package com.bitop.otcapi.constant;

public class CoinConstants {


    public static final String BUSINESSID = "ezcoins_";

    /**
     *  资产流水记录主类型
     */
    public static enum MainType  {
        /** 充币 **/
        RECHARGE("recharge", "充币"),
        /** 提币 **/
        WITHDRAWAL("withdrawal", "提币"),
        /** 系统空投 **/
        AIRDROP("airdrop", "空投"),
        /**系统**/
        SYS("sys", "系统"),
        /** 转入 **/
        TRANSFERIN("transferin", "转入"),
        /** 转出 **/
        TRANSFEROUT("transferout", "转出"),
        FEE("fee","手续费"),
        /** 冻结 **/
        FROZEN("frozen", "冻结"),
        /** 解冻 **/
        UNFREEZE("unfreeze", "解冻"),

        NORECORD("norecord", "不记录"),

        /** 锁仓 **/
        LOCKUP("lockup", "锁仓"),
        /** 解锁 **/
        UNLOCK("unlock", "解锁");

        private String type;

        private String remark;

        private MainType(String type, String remark) {
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

        public String getValue() {
            return type;
        }
    }

    /**
     * 收支类型
     */
    public static enum IncomeType  {

        /** 收入 **/
        INCOME("1"),

        /** 支出 **/
        PAYOUT("2");

        private String type;

        private IncomeType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return String.valueOf(type);
        }
    }

    /**
     *  资产流水记录 状态
     */
    public static enum RecordStatus  {
        /** 成功 **/
        OK("1", "成功"),

        /** 待审核 **/
        WAIT("2", "待审核"),

        /** 审核通过 **/
        PASS("3", "审核通过"),

        /** 审核拒绝 **/
        REFUSE("4", "审核拒绝");

        private String status;

        private String remark;

        private RecordStatus(String status, String remark) {
            this.status = status;
            this.remark = remark;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

    }
}