package com;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Kouzhao {

    RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(Kouzhao.class);
    ThreadLocal<Map<String, String>> sessionMapLocal = new ThreadLocal<>();
    ThreadLocal<String> userNameThreadLocal = new ThreadLocal<>();
    ExecutorService executor = Executors.newFixedThreadPool(20);
    Map<String, String> hostMap = new HashMap() {{
        put("463", "http://cbgl.tboedu.com:8000");
        put("640", "http://cbgl.tboedu.com:8000");
        put("1023", "http://cbwz.tboedu.com:8000");
        put("1007", "http://cbwz.tboedu.com:8000");
        put("1028", "http://cbwz.tboedu.com:8000");
    }};


    public static void main(String[] args) throws JsonProcessingException {
        Kouzhao kouzhao = new Kouzhao();

        List<MultiValueMap<String, String>> xinzheng = new ArrayList<>();
        xinzheng.add(loadMInfo("魏留玉", "41010419740717503X", "13592471691"));
//        xinzheng.add(loadMInfo("魏亚贤", "410104199710090062", "15565130887"));
//        xinzheng.add(loadMInfo("魏彩倩", "410104200101180105", "19937088059"));
//        xinzheng.add(loadMInfo("魏雅倩", "410104200007200069", "15093202047"));
//        xinzheng.add(loadMInfo("张俊红", "410104197805265022", "17788101163"));
//        xinzheng.add(loadMInfo("王坡妮", "410104193711085022", "19937035632"));
        kouzhao.execute(new String[]{"1007", "1028"}, xinzheng);


//        List<MultiValueMap<String, String>> erqi = new ArrayList<>();
//        erqi.add(loadMInfo("王晓锋", "410185199303033536", "15036016322"));
//        kouzhao.execute(new String[]{"348", "350", "371"}, xinzheng);
//        List<MultiValueMap<String, String>> jinshui = new ArrayList<>();
//        jinshui.add(loadMInfo("赵丹", "41010419910828012X", "18539973624"));
//        kouzhao2.execute(new String[]{"365", "377", "353"}, jinshui);

//        List<MultiValueMap<String, String>> guancheng = new ArrayList<>();
//        guancheng.add(loadMInfo("王霞菲", "410482199005061085", "13384008703"));
//        kouzhao2.execute(new String[]{"463"}, guancheng);


//        List<MultiValueMap<String, String>> wang = new ArrayList<>();
//        wang.add(loadMInfo("陈娟娟", "41018519710106356X", "15838260722"));
//        wang.add(loadMInfo("王琪锋", "410185200206013555", "15838260722"));
//        wang.add(loadMInfo("王新军", "410125197104033511", "15936290766"));
//        kouzhao2.execute(new String[]{"569", "615",
//                "617", "618", "640", "641", "642", "650", "651", "652", "1023"}, wang);

    }


    public void execute(String[] ids, List<MultiValueMap<String, String>> multiValueMaps) {
        multiValueMaps.forEach(multiValueMap -> {
            executor.execute(() -> {
                sessionMapLocal.set(new HashMap<>());
                userNameThreadLocal.set(multiValueMap.getFirst("name"));
                while (true) {
                    try {
                        String id = ids[new Random().nextInt(ids.length)];
                        log("抢口罩线程开始{}", id);
                        String yysjd = search(id);
                        if (yysjd != null) {
                            multiValueMap.set("yysjd", yysjd);
                            if (vie(id, multiValueMap)) {
                                log("抢到口罩了，本次结束");
                                break;
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        err(e.getMessage());
                        //e.printStackTrace();
                    }
                }
            });
        });
    }

    public String search(String id) throws JsonProcessingException {
        log("开始查询网点货源");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<String> cookies = new ArrayList<>();
        cookies.add("id=" + id);
        if (sessionMapLocal.get().containsKey(hostMap.get(id))) {
            cookies.add(sessionMapLocal.get().get(hostMap.get(id)));
            log("sessionId:" + sessionMapLocal.get().get(hostMap.get(id)));
        }
        headers.put(HttpHeaders.COOKIE, cookies);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("date", DateFormatUtils.format(DateUtils.addDays(new Date(), 1), "yyyy-MM-dd"));
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        String host = hostMap.get(id);
        if (host == null) {
            host = "http://s1.kuistar.cn:8000";
        }
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(host + "/getConfig", requestEntity, String.class);
        List<String> sess = responseEntity.getHeaders().get("Set-Cookie");
        if (sess != null) {
            String sessionId = sess.stream().filter(s -> s.startsWith("JSESSIONID")).findFirst().orElse(null);
            if (sessionId != null) {
                sessionMapLocal.get().put(hostMap.get(id), sessionId);
            }
        }
//        if (sess != null && sessionIdLocal.get() == null) {
//            sessionIdLocal.set(sess.stream().filter(s -> s.startsWith("JSESSIONID")).findFirst().orElse(null));
//        }
        String result = responseEntity.getBody();
        log(result);
        ObjectMapper mapper = new ObjectMapper();
        Map m = mapper.readValue(result, Map.class);
        if ("0".equals(m.get("result").toString())) {
            log("还没到时间。。。");
            if (m.get("msg") != null) {
                log(m.get("msg").toString());
            }
            return null;
        }

        List<Map<String, Object>> infoList = (List<Map<String, Object>>) m.get("list");

        for (int i = 0; i < infoList.size(); i++) {
            Map<String, Object> infoMap = infoList.get(i);
            int rs = Integer.parseInt(infoMap.get("rs").toString());
            int xzrs = Integer.parseInt(infoMap.get("xzrs").toString());
            if (rs != xzrs) {
                String xid = infoMap.get("id").toString();
                log("有货了，网点ID{}", xid);
                return xid;
            }
        }
        log("很遗憾，没货了。。。");
        return null;
    }

    public boolean vie(String id, MultiValueMap<String, String> params) throws IOException {
        log("开始抢口罩");
        FileUtils.write(new File("d:/kouzhaolog.txt"), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()) + ":" + params.getFirst("name") + "\n", Charset.defaultCharset(), true);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<String> cookies = new ArrayList<>();
        cookies.add("id=" + id);
        cookies.add(sessionMapLocal.get().get(hostMap.get(id)));
        headers.put(HttpHeaders.COOKIE, cookies);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        String host = hostMap.get(id);
        if (host == null) {
            host = "http://s1.kuistar.cn:8000";
        }
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(host + "/list/yy", requestEntity, String.class);
        String result = responseEntity.getBody();
        log(result);
        ObjectMapper mapper = new ObjectMapper();
        Map m = mapper.readValue(result, Map.class);
        if ("1".equals(m.get("result").toString())) {
            log("哈哈，抢到口罩了");
            FileUtils.write(new File("d:/kouzhao.txt"), userNameThreadLocal.get() + "  " + DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()) + ":" + result + "\n", Charset.defaultCharset(), true);
            return true;
        }
        log("很遗憾，没有抢到口罩-_--");
        return false;
    }

    public static MultiValueMap<String, String> loadMInfo(String name, String card, String phone) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", name);
        params.add("card", card);
        params.add("phone", phone);
        params.add("fkwz", "2");
        params.add("yyrq", DateFormatUtils.format(DateUtils.addDays(new Date(), 1), "yyyy-MM-dd"));
        return params;
    }

    public void log(String content, Object... arguments) {
        String info = userNameThreadLocal.get() + "  -  " + content;
        logger.info(info, arguments);
        try {
            FileUtils.write(new File("d:/log.txt"), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()) + ":" + info + "\n", Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void err(String content, Object... arguments) {
        String errorInfo = userNameThreadLocal.get() + "  -  " + content;
        logger.error(errorInfo, arguments);
        try {
            FileUtils.write(new File("d:/errlog.txt"), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()) + ":" + errorInfo + "\n", Charset.defaultCharset(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
