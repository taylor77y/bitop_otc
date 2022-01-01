package com.bitop.otcapi.exception;

/**
 * 资金账户繁忙 异常类
 *
 */
public class AccountOperationBusyException extends CoinException
{
    private static final long serialVersionUID = 1L;

    /**
     * 资金账户繁忙
     */
    public AccountOperationBusyException()
    {
        super("500","资金账户繁忙", null);
    }
}