package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.OtcCoinType;

import java.util.List;

public interface OtcCoinTypeService extends IService<OtcCoinType>  {

    void updateCoinTypeStatusById(String id, String status);

//  public List<OtcCoinType> queryAllCoins();

    public List<OtcCoinType> queryAllCoinsFromHuobi();

//    List<OtcCoinType> fiatList();

    List<OtcCoinType> fiatListFromBinance();

    boolean statusService(OtcCoinType coinType,Integer type);
}
