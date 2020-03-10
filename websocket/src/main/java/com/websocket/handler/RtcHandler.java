package com.websocket.handler;

import com.common.util.SpringContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/{sid}")
@Component
public class RtcHandler {

    private static final Logger log = LoggerFactory.getLogger(RtcHandler.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, RtcHandler> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";

    private Map userInfo = new HashMap();

//    public RtcHandler() {
//        objectMapper = SpringContext.getBean(ObjectMapper.class);
//    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {

        ObjectMapper objectMapper = getObjectMapper();

        this.session = session;
        this.userId = session.getId();
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            //加入set中
        } else {
            webSocketMap.put(userId, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }
        userInfo.put("userId", userId);

        log.info("用户连接:" + userId + ",当前在线人数为:" + getOnlineCount());
        try {
            Map infoMap = new HashMap();
            infoMap.put("onlineCount", getOnlineCount());
            infoMap.put("userInfo", userInfo);
            String message = "{\"event\":\"open\",\"message\":" + objectMapper.writeValueAsString(infoMap) + "}";
            sendMessage(message);
        } catch (IOException e) {
            log.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        Map infoMap = new HashMap();
        infoMap.put("onlineCount", getOnlineCount());
        infoMap.put("userInfo", userInfo);
        String message = "{\"event\":\"leave\",\"message\":" + objectMapper.writeValueAsString(infoMap) + "}";
        onMessage(message);
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        RtcHandler that = this;
        log.info("用户消息:" + userId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {

            ObjectMapper objectMapper = getObjectMapper();
            Map infoMap = objectMapper.readValue(message, Map.class);
            Object sendTo = infoMap.get("sendTo");
            if (sendTo != null) {
                webSocketMap.get(sendTo).sendMessage(message);
                return;
            }
            webSocketMap.forEach((k, y) -> {
                try {
                    if (y != that) {
                        y.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("name") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        RtcHandler.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        RtcHandler.onlineCount--;
    }

    private ObjectMapper getObjectMapper() {
        return SpringContext.getBean(ObjectMapper.class);
    }
}
