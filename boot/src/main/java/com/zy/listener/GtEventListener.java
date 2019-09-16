package com.zy.listener;

import com.zy.bo.AppNoticeBo;
import com.zy.bo.OaMessage;
import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONArray;
import com.gexin.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;
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
public class GtEventListener {

    private static final Logger logger = LoggerFactory.getLogger(GtEventListener.class);

    @Value("${gt.appId}")
    private String appId;
    @Value("${gt.appKey}")
    private String appKey;
    @Value("${gt.masterSecret}")
    private String masterSecret;
    @Value("${gt.url}")
    private String url;
    @Value("${gt.host}")
    private String host;
    @Value("${notice.server.url}")
    private String noticeServerUrl;

    private List<String> serverUrls;

    private IGtPush iGtPush;

    @PostConstruct
    public IGtPush init() {
        if (!StringUtils.isEmpty(noticeServerUrl)) {
            serverUrls = Arrays.asList(noticeServerUrl.split(","));
        }
        System.setProperty("gexin_pushList_needDetails", "true");    // 配置返回每个用户返回用户状态，可选
        System.setProperty("gexin_pushList_needAliasDetails", "true");  // 配置返回每个别名及其对应cid的用户状态，可选
        try {
            iGtPush = new IGtPush(url, appKey, masterSecret);
        } catch (Exception e) {
            logger.error("推送服务器连接失败,请检查网络连接状态!", e);
        }
        return iGtPush;
    }

    @Autowired
    RestTemplate restTemplate;

    /**
     * 每隔秒钟发10送一次请求数据
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void push() {
        if (serverUrls != null && serverUrls.size() > 0) {
                for (String url : serverUrls) {
                try {
                    List<AppNoticeBo> appNoticeBos = getMessage(url);
                    if (appNoticeBos != null && appNoticeBos.size() > 0) {
                        for (AppNoticeBo appNoticeBo : appNoticeBos) {
                            pushMessage(appNoticeBo);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.toString());
                }
            }
        }
    }

    public void pushMessage(AppNoticeBo appNoticeBo) {
        if (iGtPush == null) {
            logger.error("推送服务器连接失败,请检查网络连接状态!");
            return;
        }
        try {
            List<Target> targetList = appNoticeBo.getTargets();
            OaMessage oaMessage = appNoticeBo.getMessage();
            ListMessage message = getGtMessage(oaMessage);

            logger.info("开始发送消息");
            String taskId = iGtPush.getContentId(message);
            IPushResult ret = iGtPush.pushMessageToList(taskId, targetList);
            logger.info("app消息推送成功!------" + ret.getResponse().toString());
        } catch (Exception e) {
            logger.error("app消息推送失败!------------", e);
        }
    }


    public List<AppNoticeBo> getMessage(String url) {

        try {
            String result = restTemplate.postForObject(url, HttpEntity.EMPTY, String.class);
            logger.info("返回数据：" + result);
            JSONObject jsonObject = JSON.parseObject(result);
            Boolean success = jsonObject.getBoolean("success");
            if (!success) {
                throw new RuntimeException("数据获取失败，" + jsonObject.toJSONString());
            }
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            List<AppNoticeBo> appNoticeBos = JSON.parseArray(jsonArray.toJSONString(), AppNoticeBo.class);

            return appNoticeBos;
        } catch (Exception e) {

            throw new RuntimeException("数据获取失败：" + e.toString());
        }
    }

    public ListMessage getGtMessage(OaMessage oaMessageVo) {

        String title = null;
        if ("0".equals(oaMessageVo.getType())) {
            title = "电子政务 - 业务流程";
        } else if ("1".equals(oaMessageVo.getType())) {
            title = "电子政务 - 邮件";
        } else if ("2".equals(oaMessageVo.getType())) {
            title = "电子政务 - 通知公告";
        }

        Style0 style = new Style0();
        style.setLogo("icon.png");  // 配置通知栏图标
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);

        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(oaMessageVo.getSummary());

        NotificationTemplate template = new NotificationTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(1);
        template.setTransmissionContent(JSON.toJSONString(oaMessageVo));
        template.setStyle(style);

        ListMessage message = new ListMessage();    // 通知透传模板
        message.setOffline(true);   // 设置消息离线，并设置离线时间
        message.setOfflineExpireTime(24 * 1000 * 3600);   // 离线有效时间，单位为毫秒，可选
        message.setData(template);

        return message;
    }
}
