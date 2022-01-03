package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.exception.AccountBalanceNotEnoughException;
import com.bitop.otcapi.exception.AccountOperationBusyException;
import com.bitop.otcapi.fcg.entity.CoinAccount;
import com.bitop.otcapi.fcg.entity.vo.BalanceChange;

import java.util.List;

public interface CoinAccountService extends IService<CoinAccount> {

    // 同步操作资产[余额/冻结/锁仓]
    boolean balanceChangeSYNC(List<BalanceChange> cList)
            throws AccountBalanceNotEnoughException, AccountOperationBusyException;

    CoinAccount getAccountByUserIdAndCoinId(String userId, String coinName,String userName) throws AccountOperationBusyException;

    List<CoinAccount> processCoinAccount(String userId,String userName);
}
