package com.bitop.otcapi.fcg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bitop.otcapi.constant.CoinConstant;
import com.bitop.otcapi.fcg.entity.CoinType;
import com.bitop.otcapi.fcg.mapper.CoinTypeMapper;
import com.bitop.otcapi.fcg.service.CoinTypeService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CoinTypeServiceImpl extends ServiceImpl<CoinTypeMapper, CoinType> implements CoinTypeService {

    @Value("${openapi.url.huobi}")
    private String huobiUrl;

    @Value("${openapi.url.binance}")
    private String binanceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CoinTypeMapper coinTypeMapper;


    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
    }


    @Override
    public void updateCoinTypeStatusById(String id, String status){
        coinTypeMapper.updateStatusById(id,status);
    }


    @Override
    public List<CoinType> queryAllCoinsFromHuobi() {

        ResponseEntity<String> response = restTemplate.getForEntity(huobiUrl, String.class);
        String transferResponse = response.getBody();
        log.debug("Received {}", transferResponse);

        Map<String, Object> transferResponseMap;
        try {
            transferResponseMap = objectMapper.readValue(transferResponse, Map.class);
        } catch (IOException e) {
            log.error("Failed to convert json to map", e);
            return Collections.emptyList();
        }
        return (List<CoinType>)transferResponseMap.get("data");
    }

    /**
     * 从币安接口爬取法币币种列表
     *
     * @param
     * @return
     */
    @Override
    public List<CoinType> fiatListFromBinance() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(binanceUrl, request, String.class);

        String transferResponse = response.getBody();
        log.debug("Received {}", transferResponse);

        Map<String, Object> transferResponseMap;
        try {
            transferResponseMap = objectMapper.readValue(transferResponse, Map.class);
        } catch (IOException e) {
            log.error("Failed to convert json to map", e);
            return Collections.emptyList();
        }
        return (List<CoinType>)transferResponseMap.get("data");
    }

    @Override
    public boolean statusService(CoinType coinType,Integer type) {
        if (type.equals(CoinConstant.OTC_STATUS)) {
            if ("1".equals(coinType.getOtcStatus())) {
                return false;
            } else {
                return true;
            }
        }
        if (type.equals(CoinConstant.COIN_STATUS)) {
            if ("1".equals(coinType.getStatus())) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
