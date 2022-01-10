package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.context.ContextHandler;
import com.bitop.otcapi.fcg.entity.OtcConfigNext;
import com.bitop.otcapi.fcg.entity.req.OtcConfigNextReqDto;
import com.bitop.otcapi.fcg.mapper.OtcConfigNextMapper;
import com.bitop.otcapi.fcg.service.OtcConfigNextService;
import com.bitop.otcapi.response.Response;
import com.bitop.otcapi.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OtcConfigNextServiceImpl extends ServiceImpl<OtcConfigNextMapper, OtcConfigNext> implements OtcConfigNextService {

    /***
     * @Description: 新增 次级菜单-OTC 配置
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: taylor
     * @Date: 2021/12/3
     * @param otcConfigNextReqDto
     */
    @Override
    public Response addOrUpdateOtcConfig(OtcConfigNextReqDto otcConfigNextReqDto) {
        String userId = ContextHandler.getUserId();
        OtcConfigNext otcConfigNext = new OtcConfigNext();
        BeanUtils.copyProperties(otcConfigNextReqDto, otcConfigNext);
//        otcConfig.setUserId(userId);
        if (StringUtils.isEmpty(otcConfigNextReqDto.getId())){
            baseMapper.insert(otcConfigNext);
        }else {
            baseMapper.updateById(otcConfigNext);
        }
        return Response.success();
    }
}
