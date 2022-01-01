package com.bitop.otcapi.constant;

public interface RecordSonType {
    public static final String ORDINARY_WITHDRAWAL= "1";//普通提币
    public static final String ORDINARY_RECHARGE = "2";//普通冲币

    public static final String SYS_AIRPORT = "3";//系统空投
    public static final String SYS_DEDUCTION = "4";//系统扣除

    public static final String TRANSFER_IN = "5";//转入
    public static final String TRANSFER_OUT = "6";//转出

    public static final String HANDLING_FEE="7";//手续费

    public static final String TRANSACTION_FREEZE="8";//交易冻结

    public static final String TRANSACTION_UNFREEZE="9";//交易解冻

}
