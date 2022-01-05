package com.bitop.otcapi.fcg.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.CoinConstants;
import com.bitop.otcapi.constant.CoinStatus;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.exception.AccountBalanceNotEnoughException;
import com.bitop.otcapi.exception.AccountOperationBusyException;
import com.bitop.otcapi.fcg.entity.CoinAccount;
import com.bitop.otcapi.fcg.entity.CoinRecord;
import com.bitop.otcapi.fcg.entity.CoinType;
import com.bitop.otcapi.fcg.entity.vo.BalanceChange;
import com.bitop.otcapi.fcg.mapper.CoinAccountMapper;
import com.bitop.otcapi.fcg.service.CoinAccountService;
import com.bitop.otcapi.fcg.service.CoinRecordService;
import com.bitop.otcapi.fcg.service.CoinTypeService;
import com.bitop.otcapi.redis.CacheUtils;
import com.bitop.otcapi.util.SpringUtils;
import com.bitop.otcapi.websocket.WebSocketHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CoinAccountServiceImpl extends ServiceImpl<CoinAccountMapper, CoinAccount> implements CoinAccountService {


    @Autowired
    private CoinRecordService recordService;

    @Autowired
    private CoinTypeService typeService;

    @Autowired
    private CacheUtils cacheUtils;


    @Override
    public boolean balanceChangeSYNC(List<BalanceChange> cList) throws AccountBalanceNotEnoughException, AccountOperationBusyException {
        if (StringUtils.isEmpty(cList)) {
            return false;// 无操作
        }
        for (BalanceChange c : cList) {
            // 判断参数
            if (StringUtils.isEmpty(c.getCoinName()) || StringUtils.isEmpty(c.getUserId())|| StringUtils.isEmpty(c.getMainType())) {
                log.error("参数异常{}", JSON.toJSON(c));
                return false;
            }
            if (StringUtils.isEmpty(c.getAvailable()) && StringUtils.isEmpty(c.getFrozen()) && StringUtils.isEmpty(c.getLockup())) {
                log.error("参数异常{}", JSON.toJSON(c));
                return false;
            }

            String userName= ContextHandler.getUserName();
            if (StringUtils.isEmpty(userName)){
//                EzUser byId = userService.getById(c.getUserId());
//                if (byId==null){
//                    log.error("参数异常{}",c.getUserId());
//                    return false;
//                }
//                userName=byId.getUserName();
            }
            CoinAccount acc = getAccountByUserIdAndCoinId(c.getUserId(), c.getCoinName(), userName); //通过用户id和币种id查询账户
            if (StringUtils.isEmpty(acc)) {
                log.error("参数异常-{},找到不到账户", JSON.toJSON(c));
                return false;
            }
            log.debug("an act or the right of selecting something from among a group of alternatives");
            log.info("操作用户ID[{}]币种ID[{}-{}]余额数量[{}]冻结数量[{}]锁仓数量[{}],操作主类型[{}],操作子类型[{}]", c.getUserId(), c.getCoinName(), c.getCoinName(),
                    c.getAvailable(), c.getFrozen(), c.getLockup(), c.getMainType(), c.getSonType());
            if (StringUtils.isEmpty(c.getAvailable()) && c.getAvailable().compareTo(BigDecimal.ZERO) != 0) {//判断 操作剩余金额
                if (acc.getAvailable().add(c.getAvailable()).compareTo(BigDecimal.ZERO) < 0) {
                    throw new AccountBalanceNotEnoughException();
                } else {
                    acc.setAvailable(acc.getAvailable().add(c.getAvailable()));
                }
            }
            if (StringUtils.isEmpty(c.getFrozen()) && c.getFrozen().compareTo(BigDecimal.ZERO) != 0) {//判断 操作冻结金额
                if (acc.getFrozen().add(c.getFrozen()).compareTo(BigDecimal.ZERO) < 0) {
                    throw new AccountBalanceNotEnoughException();
                } else {
                    acc.setFrozen(acc.getFrozen().add(c.getFrozen()));
                }
            }
            if (StringUtils.isEmpty(c.getLockup()) && c.getLockup().compareTo(BigDecimal.ZERO) != 0) { //判断 操作锁仓金额
                if (acc.getLockup().add(c.getLockup()).compareTo(BigDecimal.ZERO) < 0) {
                    throw new AccountBalanceNotEnoughException();
                } else {
                    acc.setLockup(acc.getLockup().add(c.getLockup()));
                }
            }
            c.setCoinName(acc.getCoinName());
            boolean isLock = cacheUtils.getAccountLock(acc.getId(), CacheUtils.LOCK_WAITTIME_SECONDS);//获得锁
            try {
                if (!isLock || baseMapper.updateById(acc) <= 0) {
                    throw new AccountOperationBusyException();
                    //
                } else {//CoinConstants.MainType.FROZEN.getType().equals(c.getMainType()) || CoinConstants.MainType.UNFREEZE.getType().equals(c.getMainType())
                    if (CoinConstants.MainType.LOCKUP.getType().equals(c.getMainType()) || CoinConstants.MainType.UNLOCK.getType().equals(c.getMainType()) ||
                            CoinConstants.MainType.NORECORD.getType().equals(c.getMainType())) {
                        // 冻结/解冻/锁仓/解锁 不生成资产流水
                    } else if (CoinConstants.MainType.RECHARGE.getType().equals(c.getMainType())
                            || CoinConstants.MainType.WITHDRAWAL.getType().equals(c.getMainType())) {
                        // 充值/提现 单独生成资产流水
                    } else {
                        if (c.getAvailable().compareTo(BigDecimal.ZERO)!=0){
                            CoinRecord rec = new CoinRecord();
                            rec.setUserId(c.getUserId());
                            rec.setCoinName(c.getCoinName());
                            rec.setFee(c.getFee());
                            rec.setMemo(c.getMemo());
                            rec.setIncomeType(c.getIncomeType());
                            rec.setMainType(c.getMainType());
                            rec.setSonType(c.getSonType());
                            rec.setStatus(CoinConstants.RecordStatus.OK.getStatus());
                            rec.setAmount(c.getAvailable());
                            rec.setCreateBy(acc.getCreateBy());
                            recordService.save(rec);
                            WebSocketHandle.accountChange(c.getUserId(),c.getCoinName(),c.getAvailable(),c.getSonType());
                        }

                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (isLock) {
                    cacheUtils.releaseAccountLock(acc.getId());
                }
            }
        }
        return true;
    }


    @Override
    public CoinAccount getAccountByUserIdAndCoinId(String userId, String coinName,String userName) throws AccountOperationBusyException {
        LambdaQueryWrapper<CoinAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CoinAccount::getCoinName, coinName);
        lambdaQueryWrapper.eq(CoinAccount::getUserId, userId);
        CoinAccount account = baseMapper.selectOne(lambdaQueryWrapper);
//        Optional<Account> accountOpt = Optional.of(account);
//        Account account1 =  accountOpt.orElse(null);
        if (account == null) {
            //没有查到就去创建
            List<CoinAccount> accountList = processCoinAccount(userId,userName);
            if (!CollectionUtils.isEmpty(accountList)) {
                for (CoinAccount a : accountList) {
                    if (a.getCoinName().equals(coinName)) {
                        return a;
                    }
                }
            }
            return null;
        }
        return account;
    }


    /**
     * 创建用户 【资金账户】
     */
    @Override
    public List<CoinAccount> processCoinAccount(String userId,String userName) throws AccountOperationBusyException {
        LambdaQueryWrapper<CoinType> typeQueryWrapper = new LambdaQueryWrapper<>();
        typeQueryWrapper.eq(CoinType::getStatus, CoinStatus.ENABLE.getCode());
        List<CoinType> coinList = typeService.list(typeQueryWrapper);//查询到所有启用的币种

        LambdaQueryWrapper<CoinAccount> accountQueryWrapper = new LambdaQueryWrapper<>();
        accountQueryWrapper.eq(CoinAccount::getUserId, userId);
        List<CoinAccount> accountList = baseMapper.selectList(accountQueryWrapper);//查询用户的账户列表

        if (!CollectionUtils.isEmpty(coinList)) {
            if (StringUtils.isEmpty(accountList) || accountList.size() < coinList.size()) {//用户没有账户或者账户数量小于币种数量
                if (accountList == null) {
                    accountList = new ArrayList<>();
                }
                String lockName = "userLock:" + userId;
                boolean isLock = cacheUtils.getLock(lockName, CacheUtils.LOCK_WAITTIME_SECONDS);//获得锁
                try {
                    if (isLock) { //获取锁成功
                        DataSourceTransactionManager transactionManager =  SpringUtils.getBean("transactionManager1");
                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        def.setPropagationBehavior(TransactionDefinition.ISOLATION_READ_COMMITTED);
                        TransactionStatus status = transactionManager.getTransaction(def);
                        try {
                            for (CoinType coin : coinList) {
                                LambdaQueryWrapper<CoinAccount> queryWrapper = new LambdaQueryWrapper<>();//
                                queryWrapper.eq(CoinAccount::getUserId, userId);// 遍历启用的币种
                                queryWrapper.eq(CoinAccount::getCoinName, coin.getCoinName());
                                CoinAccount account = baseMapper.selectOne(queryWrapper);//通过用户id和币种id查询账户
                                if (account == null && CoinStatus.ENABLE.getCode().equals(coin.getStatus())) {/** 币种状态（0启用 1禁用 ） */
                                    Date d = new Date();
                                    account = new CoinAccount();
                                    //封装新账户数据
                                    account.setUserId(userId);
                                    account.setCoinId(coin.getId());
                                    account.setCoinName(coin.getCoinName());
                                    account.setCreateTime(d);
                                    account.setUpdateTime(d);
                                    account.setCreateBy(userName);
                                    baseMapper.insert(account);//添加账户数据
                                    accountList.add(account);//将创建好的账户放入集合
                                }
                            }
                            transactionManager.commit(status); //提交事务
                            return accountList; //返回账户列表
                        } catch (Exception e) {
                            e.printStackTrace();
                            transactionManager.rollback(status); //事务回滚
                            throw e;
                        }
                    }
                    throw new AccountOperationBusyException();
                } catch (Exception e) {
                    throw e;
                } finally {
                    if (isLock) {
                        cacheUtils.releaseLock(lockName); //释放锁
                    }
                }
            } else {
                return accountList;
            }
        }
        return new ArrayList<CoinAccount>(); //无启用币种时候
//        return Collections.EMPTY_LIST;
    }
}
