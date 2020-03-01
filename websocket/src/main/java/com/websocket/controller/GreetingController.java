package com.websocket.controller;

import com.websocket.bo.ClientMessage;
import com.websocket.bo.ServerMessage;
import com.websocket.config.GlobalConsts;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping(GlobalConsts.HELLO_MAPPING)
    @SendTo(GlobalConsts.TOPIC)
    public ServerMessage greeting(ClientMessage message) throws Exception {
        return new ServerMessage(HtmlUtils.htmlEscape(message.getName()));
    }

    @MessageMapping(GlobalConsts.RTC_MAPPING)
    @SendTo(GlobalConsts.TOPIC_RTC)
    public Object rtc(String message) {
        return message;
    }
}