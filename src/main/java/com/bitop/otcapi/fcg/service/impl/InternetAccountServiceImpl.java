package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.InternetAccount;
import com.bitop.otcapi.fcg.entity.req.InternetAccountReqDto;
import com.bitop.otcapi.fcg.entity.resp.InternetAccountRespDto;
import com.bitop.otcapi.fcg.mapper.InternetAccountMapper;
import com.bitop.otcapi.fcg.service.InternetAccountService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class InternetAccountServiceImpl extends ServiceImpl<InternetAccountMapper, InternetAccount> implements InternetAccountService {


    /**
     * 用户 网络账号列表
     *
     * @param
     * @return
     */
    @Override
    public List<InternetAccountRespDto> internetAccountList(String userId) {
        LambdaQueryWrapper<InternetAccount> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InternetAccount::getUserId, userId);
        List<InternetAccount> lists = baseMapper.selectList(lambdaQueryWrapper);
        return BeanUtils.copyListProperties(lists, InternetAccountRespDto::new);
    }


    @Override
    public Response addOrUpdateInternetAccount(InternetAccountReqDto internetAccountReqDto) {
        String userId = ContextHandler.getUserId();
        InternetAccount internetAccount = new InternetAccount();
        BeanUtils.copyProperties(internetAccountReqDto,internetAccount);
        internetAccount.setUserId(userId);
        if (StringUtils.isEmpty(internetAccountReqDto.getId())){
            baseMapper.insert(internetAccount);
        }else {
            baseMapper.updateById(internetAccount);
        }
        return Response.success();
    }



    @Override
    public Response updateUserInternetAccountStatus(InternetAccountReqDto internetAccountReqDto) {
        String userId = ContextHandler.getUserId();
        InternetAccount internetAccount = new InternetAccount();
        BeanUtils.copyProperties(internetAccount,internetAccountReqDto);
//        internetAccount.setUserId(userId);
        if (StringUtils.hasLength(internetAccountReqDto.getId())){
            baseMapper.updateById(internetAccount);
        }
        return Response.success();
    }

}
