package com.bitop.otcapi.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 汇率调用示例代码 － 聚合数据
 * 在线接口文档：http://www.juhe.cn/docs/80
 **/

public class MoneyChangeUtils {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";


    public static void main(String[] args) {
        System.out.println(getRequest3());
        System.out.println(getUSDToCNY());
    }

    public static BigDecimal getUSDToCNY() {
//        String url = "https://api.it120.cc/gooking/forex/rate?fromCode=CNY&toCode=USD";
        String result = null;
        try {
            result = HttpUtils.sendGet("https://api.it120.cc/gooking/forex/rate","fromCode=CNY&toCode=USD");
            Map map = (Map) JSONObject.parse(result);
            Integer code = (Integer) map.get("code");
            if (code != 0) {
                return BigDecimal.valueOf(6.4);
            }
            JSONObject data =(JSONObject)map.get("data");
            return (BigDecimal)data.get("rate");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 请求美元和人民币汇率
     * @return
     */
    public static String getRequest3() {
        String result = null;
        String url = "http://data.bank.hexun.com/other/cms/fxjhjson.ashx?callback=PereMoreData";//请求接口地址
        Map params = new HashMap();//请求参数
        try {
            result = net(url, params, "GET");
            if (result.contains("USD")) {
                //改成json解析更好
                int base = result.indexOf("[");
                int end = result.indexOf("]");
                String alldata = result.substring(base + 1, end);
                String rate = "";
                String[] currencys = alldata.split(",");
                for (int i = 0; i < currencys.length; i++) {
                    if (currencys[i].contains("USD") || currencys[i].contains("usd")) {
                        rate = currencys[i - 1];
                        break;
                    }
                }
                base = rate.indexOf("'");
                end = rate.lastIndexOf("'");
                float res = Float.parseFloat(rate.substring(base + 1, end)) / 100;
                return String.valueOf(res);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取每个月一号的日期格式
     *
     * @return
     */
    public static String getMoneyBaseDate() {
        DateFormat fmtDateTime = new SimpleDateFormat("YYYYMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        String res = fmtDateTime.format(calendar.getTime());
        System.out.println(res);
        return res;

    }


    /**
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return 网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map params, String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if (method == null || method.equals("GET")) {
                strUrl = strUrl + "?" + urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method == null || method.equals("GET")) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params != null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
