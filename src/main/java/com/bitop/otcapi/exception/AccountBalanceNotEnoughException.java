package com.bitop.otcapi.exception;


/**
 * 账户余额不够
 */
public class AccountBalanceNotEnoughException extends CoinException {

    private static final long serialVersionUID = 1L;

    /**
     * 账户余额不够
     */
    public AccountBalanceNotEnoughException(){
        super("500","余额不足", null);
    }
}
