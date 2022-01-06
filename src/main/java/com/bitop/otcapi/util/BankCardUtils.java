package com.bitop.otcapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class BankCardUtils {

    private static final String BANK_CODE_PATH = "/var/aipay/config/bankcode.conf";

    private static final Map<String, String> BASE_BANK_CODES =
            ImmutableMap.<String, String> builder()
                    .put("BOC", "中国银行")
                    .put("SPDB", "浦发银行")
                    .put("CMBC", "民生银行")
                    .put("CMB", "招商银行")
                    .put("CCB", "建设银行")
                    .put("ABC", "农业银行")
                    .put("ICBC", "工商银行")
                    .put("BCM", "交通银行")
                    .put("HXB", "华夏银行")
                    .put("PSBC", "邮政储蓄银行")
                    .put("PAB", "平安银行")
                    .put("CGB", "广发银行")
                    .put("CEB", "光大银行")
                    .put("BRCB", "北京农商行")
                    .put("CIB", "兴业银行")
                    .put("CNCB", "中信银行")
                    .put("BCCB", "北京银行")
                    .put("SHB", "上海银行")
                    .put("GCB", "广州银行")
                    .put("HKB", "汉口银行")
                    .put("HRBB", "哈尔滨银行")
                    .put("CZB", "浙商银行")
                    .put("HSB", "徽商银行")
                    .put("NJCB", "南京银行")
                    .put("CSCB", "长沙银行")
                    .put("QDCCB", "青岛银行")
                    .put("NBCB", "宁波银行")
                    .put("JSB", "江苏银行")
                    .put("BJRCB", "北京农村商业银行")
                    .put("SHRCB", "上海农村商业银行")
                    .put("WHRCB", "武汉农村商业银行")
                    .put("SRCB", "深圳农村商业银行")
                    .put("GDRCU", "广东省农村信用社")
                    .build();

    public static final Map<String, String> BANK_CODES;
    static {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder();
        BASE_BANK_CODES.entrySet().forEach(entry -> builder.put(entry.getKey(), entry.getValue()));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> bankCodeConf = objectMapper.readValue(new FileReader(BANK_CODE_PATH), Map.class);
            log.debug("Loaded bank code config: {}", bankCodeConf);

            bankCodeConf.keySet().forEach(bankCode -> {
                if (BASE_BANK_CODES.containsKey(bankCode)) {
                    return;
                }
                builder.put(bankCode, (String) bankCodeConf.get(bankCode));
            });
        } catch (IOException e) {
            log.warn("Failed to load bankcard config - {}", e.getMessage());
        }

        BANK_CODES = builder.build();
    }
}
