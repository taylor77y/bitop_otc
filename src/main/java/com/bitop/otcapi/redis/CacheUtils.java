package com.bitop.otcapi.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;


@Component
public class CacheUtils {

    @Autowired
    private RedisCache redisCache;

    private static final Logger log = LoggerFactory.getLogger(CacheUtils.class);

    /**
     * 锁定600s
     */
    public final static int LOCK_WAITTIME_SECONDS = 600;

    /**
     * 缓存列表
     */
    private static final String CACHE_LIST = "Cache_";

    /**
     * 用户列表
     */
    private static final String USER_LIST = "User_";

    /**
     * 账号锁列表
     */
    private static final String ACCOUNTLOCK_LIST = "AccLock_";

    /**
     * 获取账号锁key
     */
    public static String getAccountLockKey(String accountId) {
        return ACCOUNTLOCK_LIST + accountId;
    }

    /**
     * 获取用户Cache key。
     */
    public static String getUserCacheKey(String cacheId) {
        if (StringUtils.isEmpty(cacheId)) {
            throw new IllegalArgumentException("Cache id can not be null!");
        }
        return USER_LIST + cacheId;
    }

    /**
     * 获取缓存列表key。
     */
    public static String getCacheListKey(String cacheType) {
        if (StringUtils.isEmpty(cacheType)) {
            throw new IllegalArgumentException("Cache type can not be null!");
        }
        return CACHE_LIST + cacheType;
    }

    /**
     * 获取指定类型的锁key
     */
    public static String getLockKey(String lockType, String lockName) {
        if (StringUtils.isEmpty(lockType) || StringUtils.isEmpty(lockName)) {
            throw new IllegalArgumentException("Lock type or lock name can not be null!");
        }
        return lockType + "_" + lockName;
    }

    /**
     * 获取指定锁
     */
    public boolean getLock(String lockName, int expireSeconds) {
        log.info("lockName[{}], expireSeconds[{}]", lockName, expireSeconds);

        try {
            return redisCache.getLock(lockName, lockName, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            // 删除锁
            redisCache.deleteObject(lockName);
            throw e;
        }
    }

    /**
     * 释放指定锁
     */
    public boolean releaseLock(String lockName) {
        log.info("lockName[{}]", lockName);
        try {
            return redisCache.deleteObject(lockName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 锁定账户
     */
    public boolean getAccountLock(String accountId, int lockSeconds) {
        log.info("accountId[{}], lockSeconds[{}]", accountId, lockSeconds);
        String accountLockKey = CacheUtils.getAccountLockKey(accountId);
        try {
            return redisCache.getLock(accountLockKey, accountLockKey, lockSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            // 删除锁
            redisCache.deleteObject(accountLockKey);
            throw e;
        }
    }

    /**
     * 释放账户锁
     */
    public boolean releaseAccountLock(String accountId) {
        log.debug("accountId[{}]", accountId);
        String accountLockKey = CacheUtils.getAccountLockKey(accountId);
        try {
            return redisCache.deleteObject(accountLockKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


}