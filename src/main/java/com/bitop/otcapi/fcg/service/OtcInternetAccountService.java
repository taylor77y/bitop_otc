package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcInternetAccount;
import com.bitop.otcapi.fcg.entity.req.InternetAccountReqDto;
import com.bitop.otcapi.fcg.entity.resp.InternetAccountRespDto;
import com.bitop.otcapi.response.Response;

import java.util.List;

public interface OtcInternetAccountService extends IService<OtcInternetAccount> {

    /***
     * @Description: 添加/修改 网络账号信息
     * @Param: [internetAccountReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/8
     */
    Response addOrUpdateInternetAccount(InternetAccountReqDto internetAccountReqDto);


    /***
     * @Description: 修改 网络账号状态
     * @Param: [internetAccountReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/8
     */
    Response updateUserInternetAccountStatus(InternetAccountReqDto internetAccountReqDto);

    /**
     * 用户 网络账号列表
     * @param userId
     * @return List<InternetAccountRespDto>
     */
    List<InternetAccountRespDto> internetAccountList(String userId);
}
