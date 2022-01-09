package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.MatchOrderStatus;
import com.bitop.otcapi.constant.SysOrderConstants;
import com.bitop.otcapi.constant.SysTipsConstants;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.OtcOrderAppeal;
import com.bitop.otcapi.fcg.entity.OtcOrderMatch;
import com.bitop.otcapi.fcg.entity.req.AppealReqDto;
import com.bitop.otcapi.fcg.mapper.OtcBankCardMapper;
import com.bitop.otcapi.fcg.mapper.OtcOrderAppealMapper;
import com.bitop.otcapi.fcg.service.OtcBankCardService;
import com.bitop.otcapi.fcg.service.OtcOrderAppealService;
import com.bitop.otcapi.fcg.service.OtcOrderMatchService;
import com.bitop.otcapi.manager.AsyncManager;
import com.bitop.otcapi.manager.factory.AsyncFactory;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.BeanUtils;
import com.bitop.otcapi.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
public class OtcOrderAppealServiceImpl extends ServiceImpl<OtcOrderAppealMapper, OtcOrderAppeal> implements OtcOrderAppealService {

    @Autowired
    private OtcOrderMatchService matchService;

    @Autowired
    private OtcOrderAppealMapper otcOrderAppealMapper;

    /**
     * 订单申诉
     *
     * @param appealReqDto
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1",
    public Response appeal(AppealReqDto appealReqDto) {
        //通过订单号查询到订单
        OtcOrderMatch orderMatch = matchService.getById(appealReqDto.getOrderMatchNo());
        //判断订单状态
        if (!orderMatch.getStatus().equals(MatchOrderStatus.PAID.getCode())) {
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        String userId = ContextHandler.getUserId();
        //查询用户是否已申诉
        Integer exist = otcOrderAppealMapper.existAppealedByUserIdAndNo(appealReqDto.getOrderMatchNo(),"1",userId);
        if(!ObjectUtils.isEmpty(exist)){
            return Response.error(MessageUtils.message("用户已申诉，等待处理"));
        }
        OtcOrderAppeal ezOtcOrderAppeal = new OtcOrderAppeal();
        BeanUtils.copyProperties(appealReqDto, ezOtcOrderAppeal);
        ezOtcOrderAppeal.setCreateBy(ContextHandler.getUserName());
        ezOtcOrderAppeal.setUserId(userId);
        baseMapper.insert(ezOtcOrderAppeal);
        //修改订单申诉状态
        if ("1".equals(orderMatch.getIsAppeal())) {
            orderMatch.setIsAppeal("0");
            orderMatch.setStatus(MatchOrderStatus.APPEALING.getCode());
            matchService.updateById(orderMatch);
        }
        String toUserId = userId.equals(orderMatch.getUserId()) ? orderMatch.getOtcOrderUserId() : orderMatch.getUserId();
        String toName = userId.equals(orderMatch.getUserId()) ? orderMatch.getAdvertisingName() : orderMatch.getMatchAdvertisingName();
        //发送站内信
        AsyncManager.me().execute(AsyncFactory.StationLetter(toUserId,
                SysTipsConstants.TipsType.APPEALING, orderMatch.getOrderNo(), toName));
        //给于对方和自己信号（otc聊天信息表）
        AsyncManager.me().execute(AsyncFactory.sendSysChat(userId, toUserId, orderMatch.getOrderMatchNo(),
                SysOrderConstants.SysChatMsg.APPEALING, MatchOrderStatus.APPEALING));
        return Response.success();
    }

    /**
     * 取消申诉
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//value="transactionManager1"
    public Response cancelAppeal(String id) {
        String userId=ContextHandler.getUserId();
        //通过id查询到申诉信息
        OtcOrderAppeal ezOtcOrderAppeal = baseMapper.selectById(id);
        if (!"1".equals(ezOtcOrderAppeal.getStatus())) {
            return Response.error(MessageUtils.message("申诉状态已改变"));
        }
        String orderMatchNo = ezOtcOrderAppeal.getOrderMatchNo();
        //查看订单的申诉
        OtcOrderMatch orderMatch = matchService.getById(orderMatchNo);
        if (!orderMatch.getStatus().equals(MatchOrderStatus.APPEALING.getCode())){
            return Response.error(MessageUtils.message("订单状态已发生变化"));
        }
        LambdaQueryWrapper<OtcOrderAppeal> queryWrapper = Wrappers.<OtcOrderAppeal>lambdaQuery()
                .eq(OtcOrderAppeal::getOrderMatchNo, orderMatchNo)
                .and(wq -> wq.eq(OtcOrderAppeal::getStatus, "1")
                        .or().eq(OtcOrderAppeal::getStatus, "3")
                        .or().eq(OtcOrderAppeal::getStatus, "4"));
        Integer integer = baseMapper.selectCount(queryWrapper);
        if (integer == 1) {
            orderMatch.setStatus(MatchOrderStatus.PAID.getCode());
            matchService.updateById(orderMatch);
            String toUserId = userId.equals(orderMatch.getUserId()) ? orderMatch.getOtcOrderUserId() : orderMatch.getUserId();
            String toName = userId.equals(orderMatch.getUserId()) ? orderMatch.getAdvertisingName() : orderMatch.getMatchAdvertisingName();

            AsyncManager.me().execute(AsyncFactory.StationLetter(toUserId,
                    SysTipsConstants.TipsType.APPEAL_OFF, orderMatch.getOrderNo(), toName));

            AsyncManager.me().execute(AsyncFactory.sendSysChat(orderMatch.getUserId(), orderMatch.getOtcOrderUserId(), orderMatchNo,
                    SysOrderConstants.SysChatMsg.APPEAL_OFF, MatchOrderStatus.PAID));
        }
        ezOtcOrderAppeal.setStatus("2");
        baseMapper.updateById(ezOtcOrderAppeal);
        return Response.success();
    }
}
