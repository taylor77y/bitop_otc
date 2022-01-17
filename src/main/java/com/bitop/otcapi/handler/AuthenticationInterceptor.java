package com.bitop.otcapi.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bitop.otcapi.aspectj.lang.annotation.AuthToken;
import com.bitop.otcapi.constant.LimitType;
import com.bitop.otcapi.constant.LoginType;
import com.bitop.otcapi.constant.RedisConstants;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.exception.BaseException;
import com.bitop.otcapi.exception.CheckException;
import com.bitop.otcapi.exception.TokenException;
import com.bitop.otcapi.fcg.entity.OtcUser;
import com.bitop.otcapi.fcg.entity.OtcUserLimit;
import com.bitop.otcapi.fcg.entity.OtcUserLimitLog;
import com.bitop.otcapi.fcg.service.OtcUserLimitLogService;
import com.bitop.otcapi.fcg.service.OtcUserLimitService;
import com.bitop.otcapi.fcg.service.OtcUserService;
import com.bitop.otcapi.redis.RedisCache;
import com.bitop.otcapi.security.IJWTInfo;
import com.bitop.otcapi.security.JWTHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * token注意点：
 * Authorization  token
 * AuthType  类型
 */
@Component
@Slf4j
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JWTHelper jwtHelper;

    @Resource
    private OtcUserService userService;

    @Resource
    private OtcUserLimitService limitService;

    @Resource
    private OtcUserLimitLogService limitLogService;

    @Resource
    private RedisCache redisCache;


    public static boolean flag = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (!flag) {
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AuthToken authToken = method.getAnnotation(AuthToken.class);
        if (authToken != null) {
            String token = jwtHelper.getToken(request);
            //获取token
            if (StringUtils.isEmpty(token)) {
                throw new TokenException();
            }
            //解析token
            IJWTInfo fromToken = jwtHelper.getInfoFromToken(token);
            if (StringUtils.isEmpty(fromToken)) {
                throw new TokenException();
            }
            log.info("用户{}", fromToken);
            //根据token存储的值，redis判断是否失效
            boolean checkToken = jwtHelper.verifyToken(fromToken, token);
            if (!checkToken) {
                throw new TokenException();
            }
            ContextHandler.setUserId(fromToken.getUserId());
            ContextHandler.setUserName(fromToken.getUserName());
            ContextHandler.setUserType(fromToken.getUserType());
            //APP权限
            if (fromToken.getUserType().equals(LoginType.APP.getType())) {
                OtcUser user = userService.getById(fromToken.getUserId());
                if (null == user) {
                    throw new TokenException();
                }
                if (authToken.status() && "1".equals(user.getStatus())) {
                    redisCache.deleteObject(RedisConstants.LOGIN_USER_KEY + user.getUserId() + "_" + LoginType.APP.getType());
                    throw new BaseException("用户被禁止");
                }
                if (authToken.advertisingStatus()) {
                    CheckException.check("1".equals(user.getLevel()), "请先进行高级认证", () -> {
                        log.error("请先进行高级认证");
                    });
                }
                if (authToken.kyc() && "1".equals(user.getKycStatus())) {
                    throw new BaseException(null,"700","请先完成实名认证",null);
                }
                if (!authToken.LIMIT_TYPE().equals(LimitType.NOLIMIT)) {
                    LambdaQueryWrapper<OtcUserLimitLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(OtcUserLimitLog::getIsExpire, "0");
                    lambdaQueryWrapper.eq(OtcUserLimitLog::getUserId, fromToken.getUserId());
                    lambdaQueryWrapper.eq(OtcUserLimitLog::getType, authToken.LIMIT_TYPE().getCode());
                    OtcUserLimitLog one = limitLogService.getOne(lambdaQueryWrapper);
                    if (one != null) {
                        if (one.getBanTime() != null && one.getBanTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() < new Date().getTime()) {
                            one.setIsExpire("1");
                            limitLogService.updateById(one);
                            LambdaUpdateWrapper<OtcUserLimit> queryWrapper = new LambdaUpdateWrapper<>();
                            queryWrapper.eq(OtcUserLimit::getUserId, fromToken.getUserId());
                            if (authToken.LIMIT_TYPE().equals(LimitType.LOGINLIMIT)) {
                                queryWrapper.set(OtcUserLimit::getLogin, 0);
                                user.setStatus(0);
                                userService.updateById(user);
                            } else if (authToken.LIMIT_TYPE().equals(LimitType.WITHDRAWLIMIT)) {
                                queryWrapper.set(OtcUserLimit::getWithdraw, 0);
                            } else if (authToken.LIMIT_TYPE().equals(LimitType.ORDERLIMIT)) {
                                queryWrapper.set(OtcUserLimit::getOrders, 0);
                            } else if (authToken.LIMIT_TYPE().equals(LimitType.BUSINESSLIMIT)) {
                                queryWrapper.set(OtcUserLimit::getBusiness, 0);
                            }
                            limitService.update(queryWrapper);
                            return true;
                        }
                        throw new BaseException("用户行为已被限制");
                    }
                }
            } else if (fromToken.getUserType().equals(LoginType.WEB.getType())) {
                if (!"-1".equals(authToken.CODE()) && !"admin".equals(fromToken.getUserName())) {
                    List<String> list = redisCache.getCacheObject(RedisConstants.LOGIN_MENU_CODE + fromToken.getUserName());
                    if (!hasPermissions(list, authToken.CODE())) {
                        throw new BaseException("管理员没有操作权限");
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception
            ex) throws Exception {
        ContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }

    private boolean hasPermissions(List<String> permissions, String permission) {
        return permissions.contains(permission);
    }
}
