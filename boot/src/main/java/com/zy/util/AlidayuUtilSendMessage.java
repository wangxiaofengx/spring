package com.zy.util;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlidayuUtilSendMessage {

    private static final Logger logger = LoggerFactory.getLogger(AlidayuUtilSendMessage.class);

    public static boolean sendMessageDeve(String phone, String content,String freeSignName, String templateId) {
        String rusult = null; // 结果
        // 阿里大于发短信服务路径
        String url = "http://gw.api.taobao.com/router/rest";
        System.out.println(url);
        // 服务的key
        String appkey = "23904298";
        // 服务的秘钥
        String secret = "8a1711abdff0d58b81d7c2908649a8ec";

        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);

        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        // 公共回传参数，在消息返回中，会传回该参数
        req.setExtend("123456");
        // 短信类型，传入值请填写normal
        req.setSmsType("normal");
        // 短信签名，传入的短信签名必须是在阿里大于"管理中心-短信签名管理"中的可用签名
        req.setSmsFreeSignName(freeSignName);
        // 短信模板变量，传参规则{"key","value"}
        req.setSmsParamString(content);
        // 短信接收号码，支持单个或多个号码，多个号码之间以英文逗号隔开
        req.setRecNum(phone);
        // 短信模板ID，传入的模板必须是在阿里大于"管理中心-短信模板管理"中的可用模板
        req.setSmsTemplateCode(templateId);
        try {
            AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
            rusult = rsp.getSubMsg();
            String code = rsp.getSubCode();
            logger.info("result{}", rusult);
            if (code == null) {
                return true;
            }

        } catch (Exception e) {
            logger.error("error {}", e);
            return false;
        }
        return false;
    }
}
