package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcBankCard;
import com.bitop.otcapi.fcg.entity.req.BankCardReqDto;
import com.bitop.otcapi.fcg.entity.resp.BankCardRespDto;
import com.bitop.otcapi.fcg.mapper.OtcBankCardMapper;
import com.bitop.otcapi.fcg.service.OtcBankCardService;
import com.bitop.otcapi.response.Response;

import com.bitop.otcapi.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OtcBankCardServiceImpl extends ServiceImpl<OtcBankCardMapper, OtcBankCard> implements OtcBankCardService {

    @Override
    public List<BankCardRespDto> userBankCardList(String userId) {
        LambdaQueryWrapper<OtcBankCard> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OtcBankCard::getUserId, userId);
        List<OtcBankCard> lists = baseMapper.selectList(lambdaQueryWrapper);
        return BeanUtils.copyListProperties(lists, BankCardRespDto::new);
    }

    @Override
    public Response addOrUpdateUserBankCard(BankCardReqDto bankCardReqDto) {
        String userId = ContextHandler.getUserId();
        OtcBankCard paymentBank = new OtcBankCard();
        BeanUtils.copyProperties(paymentBank,bankCardReqDto);
        paymentBank.setUserId(userId);
        if (StringUtils.isEmpty(bankCardReqDto.getId())){
//            userWalletAddr.setCreateBy(userId);
            baseMapper.insert(paymentBank);
        }else {
//            userWalletAddr.setUpdateBy(userId);
            baseMapper.updateById(paymentBank);
        }
        return Response.success();
    }


    @Override
    public Response updateUserBankCardStatus(BankCardReqDto bankCardReqDto) {
        String userId = ContextHandler.getUserId();
        OtcBankCard paymentBank = new OtcBankCard();
        BeanUtils.copyProperties(paymentBank,bankCardReqDto);
        paymentBank.setUserId(userId);
        if (StringUtils.hasLength(bankCardReqDto.getId())){
//            userWalletAddr.setCreateBy(userId);
            baseMapper.updateById(paymentBank);
        }
        return Response.success();
    }
}
