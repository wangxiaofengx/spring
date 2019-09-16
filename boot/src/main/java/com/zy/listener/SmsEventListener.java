package com.zy.listener;

import com.epoint.core.utils.code.MD5Util;
import com.epoint.core.utils.httpclient.HttpUtil;
import com.epoint.third.apache.commons.codec.binary.Base64;
import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONArray;
import com.gexin.fastjson.JSONObject;
import com.zy.bo.OaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@EnableScheduling
public class SmsEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SmsEventListener.class);

    @Autowired
    RestTemplate restTemplate;
    @Value("${sms.server.url}")
    private String smsServerUrl;
    @Value("${sms.ecName}")
    private String ecName;
    @Value("${sms.apId}")
    private String apId;
    @Value("${sms.secretKey}")
    private String secretKey;
    @Value("${sms.sign}")
    private String sign;
    @Value("${sms.getmsgUrl}")
    private String getmsgUrl;

//    @Value("${sms.templateId}")
//    private String templateId;
//
//    @Value("${sms.freeSignName}")
//    private String freeSignName;

    private List<String> serverUrls;

        @PostConstruct
        public void init() {
            if (!StringUtils.isEmpty(smsServerUrl)) {
                serverUrls = Arrays.asList(smsServerUrl.split(","));
            }
        }

        /**
         * 每隔秒钟发10送一次请求数据
         */
        @Scheduled(cron = "0/10 * * * * ?")
        public void push() {
        String  access_token="";
        if (smsServerUrl != null) {

                List<OaMessage> smsDataList = getMessage(getmsgUrl);
                 System.out.println(smsDataList.size());
                 for (int i = 0; i < smsDataList.size(); i++) {
                     System.out.println("++++++++++++++++++"+smsDataList.get(i).getYwlx());
                     System.out.println("++++++++++++++++++"+smsDataList.get(i).getBusinessKey());
                 }
                  smsDataList.forEach(oaMessage -> {
                    String content = "郑州市自规局：您有一条"+ oaMessage.getYwlx() + "未处理，受理编号：" + oaMessage.getBusinessKey() + "。";
                    System.out.println("push=============================="+content);
                    //String json=norSubmit(oaMessage.getMobilePhone(), content);
                   // String result = apiInvoke(smsServerUrl,access_token,json);
                    //System.out.println("========================================"+result);
                    //JSONObject jsonObject = JSON.parseObject(result);
                   //Boolean success = jsonObject.getBoolean("success");
                      Boolean success =false;
                    if (!success) {
                        logger.error("消息发送失败：{}", oaMessage.toString());
                    }
                });

        }
    }

    @Override
    public String toString() {
        return smsServerUrl;
    };
    public  String norSubmit(String mobiles, String content) {
        // 64base编码后的内容
        String enCode = "";
        // 集团客户名称

        // 扩展码
        String addSerial = "";
        // md5结果
        String mac = "";
        mac = MD5Util.getMD5(ecName+apId+secretKey+mobiles+content+sign+addSerial);
        String json = "{\"addSerial\":\""+addSerial+"\","
                + "\"apId\":\""+apId+"\","
                + "\"content\":\""+content+"\","
                + "\"ecName\":\""+ecName+"\","
                + "\"mac\":\""+mac+"\","
                + "\"mobiles\":\""+mobiles+"\","
                + "\"secretKey\":\""+secretKey+"\","
                + "\"sign\":\""+sign+"\"}";
        System.out.println(json);
        try {
            enCode = Base64.encodeBase64String(json.getBytes("utf-8"));
        }catch (Exception E){
           E.printStackTrace();
        }

        return enCode;
    }


    //调用最终API方法
    public static String apiInvoke(String url, String access_token, String json) {
         String result="";
        //实际需要调用的API的地址，将调用凭证token作为参数传入
        if (url.indexOf('?') > 0)
            url = url + "&access_token=" + access_token;
        else
            url = url + "?access_token=" + access_token;
        System.out.println(access_token);
        //通过SDK的HttpClientUtil调用API，获得返回值
        try {
             result =HttpUtil.doPostJson(url, json);
            System.out.println("apiInvoke========================================result"+result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result ;
    }


    public List<OaMessage> getMessage(String url) {
        try {
            String result = restTemplate.postForObject(url, HttpEntity.EMPTY, String.class);
            logger.info("返回数据：" + result);
            JSONObject jsonObject = JSON.parseObject(result);
            Boolean success = jsonObject.getBoolean("success");
            if (!success) {
                throw new RuntimeException("数据获取失败，" + jsonObject.toJSONString());
            }
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            List<OaMessage> smsDataList = JSON.parseArray(jsonArray.toJSONString(), OaMessage.class);
            return smsDataList;
        } catch (Exception e) {
            logger.error("数据获取失败：",e);
            throw new RuntimeException("数据获取失败：" + e.toString());
        }
    }
}
