package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcInternetAccount;
import com.bitop.otcapi.fcg.entity.req.InternetAccountReqDto;
import com.bitop.otcapi.fcg.entity.resp.InternetAccountRespDto;
import com.bitop.otcapi.fcg.mapper.OtcInternetAccountMapper;
import com.bitop.otcapi.fcg.service.OtcInternetAccountService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OtcInternetAccountServiceImpl extends ServiceImpl<OtcInternetAccountMapper, OtcInternetAccount> implements OtcInternetAccountService {


    @Autowired
    private OtcInternetAccountMapper otcInternetAccountMapper;

    /**
     * 用户 网络账号列表
     *
     * @param
     * @return
     */
    @Override
    public List<InternetAccountRespDto> internetAccountList(String userId) {
        LambdaQueryWrapper<OtcInternetAccount> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OtcInternetAccount::getUserId, userId);
        List<OtcInternetAccount> lists = baseMapper.selectList(lambdaQueryWrapper);
        return BeanUtils.copyListProperties(lists, InternetAccountRespDto::new);
    }


    @Override
    @Transactional
    public Response addOrUpdateInternetAccount(InternetAccountReqDto internetAccountReqDto) {
        String userId = ContextHandler.getUserId();
        OtcInternetAccount internetAccount = new OtcInternetAccount();
        BeanUtils.copyProperties(internetAccountReqDto,internetAccount);
        internetAccount.setUserId(userId);
        if (StringUtils.isEmpty(internetAccountReqDto.getId())){
            otcInternetAccountMapper.save(internetAccount);
        }else {
            otcInternetAccountMapper.updateById(internetAccount);
        }
        return Response.success();
    }



    @Override
    public Response updateUserInternetAccountStatus(InternetAccountReqDto internetAccountReqDto) {
        String userId = ContextHandler.getUserId();
        OtcInternetAccount internetAccount = new OtcInternetAccount();
        BeanUtils.copyProperties(internetAccountReqDto, internetAccount);
        internetAccount.setUserId(userId);
        if (StringUtils.hasLength(internetAccountReqDto.getId())){
            otcInternetAccountMapper.updateStatusById(internetAccount);
        }
        return Response.success();
    }

}
