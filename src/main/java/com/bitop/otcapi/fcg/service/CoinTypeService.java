package com.bitop.otcapi.fcg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bitop.otcapi.fcg.entity.CoinType;
import com.bitop.otcapi.fcg.entity.resp.DigitalCoinTypeRespDto;
import com.bitop.otcapi.fcg.entity.resp.FiatCoinTypeRespDto;

import java.util.List;

public interface CoinTypeService extends IService<CoinType>  {

    void updateCoinTypeStatusById(String id, String status);

//  public List<OtcCoinType> queryAllCoins();

    public List<DigitalCoinTypeRespDto> queryAllCoinsFromHuobi();

//    List<OtcCoinType> fiatList();

    List<FiatCoinTypeRespDto> fiatListFromBinance();

    boolean statusService(CoinType coinType,Integer type);
}
