package com.zy.util;

import com.epoint.core.utils.string.StringUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {


    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;

    /**
     * 直接以流返回
     */
    public static final int RTN_TYPE_1 = 1;
    /**
     * 直接以string返回
     */
    public static final int RTN_TYPE_2 = 2;
    /**
     * 以map返回，reslut:接口结果string;statusCode:http状态码
     */
    public static final int RTN_TYPE_3 = 3;
    /**
     * 以map返回，reslut:接口结果string;statusCode:http状态码;cookie:response的cookie
     * cookie值键值对，格式 key1=value1;key2=value2;...
     */
    public static final int RTN_TYPE_4 = 4;
    /**
     * 默认上传文件的文件流或file 的key Name
     */
    private static final String DEFAULT_BINARYBODY_KEYNAME = "file";

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        // 在提交请求之前 测试连接是否可用
        connMgr.setValidateAfterInactivity(1);

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET请求
     *
     * @param apiUrl
     *            API接口URL
     * @return String 响应内容
     */
    public static String doGet(String apiUrl) {
        return doHttp(apiUrl, null, "get", RTN_TYPE_2);
    }

    /**
     * 发送POST请求
     *
     * @param apiUrl
     *            API接口URL
     * @param params
     *            K-V参数
     * @return String 响应内容
     */
    public static String doPost(String apiUrl, Map<String, Object> params) {
        return doHttp(apiUrl, params, "post", RTN_TYPE_2);
    }

    /**
     * 发送POST请求
     *
     * @param apiUrl
     *            API接口URL
     * @param json
     *            json参数
     * @return String 响应内容
     */
    public static String doPostJson(String apiUrl, String json) {
        return doHttp(apiUrl, json, "post", RTN_TYPE_2);
    }

    /**
     * 发送 http 请求
     *
     * @param apiUrl
     *            API接口URL
     * @param params
     *            {Map<String, Object> K-V形式、json字符串}
     * @param method
     *            {null、或者post:POST请求、patch:PATCH请求、delete:DELETE请求、get:GET请求}
     * @param type
     *            {HttpUtil.RTN_TYPE_1:请求返回stream(此时流需要在外部手动关闭);HttpUtil.
     *            RTN_TYPE_2:string;HttpUtil.RTN_TYPE_3:返回一个map,map包含结果(
     *            结果是string形式)以及http状态码;HttpUtil.RTN_TYPE_4:返回一个map,map包含结果(
     *            结果是string形式), http状态码和cookie;其他情况返回string}
     *            如果结果是个map,key为:result,statusCode,cookie,分别返回 结果
     *            string,http状态码，cookie; cookie值键值对，格式
     *            key1=value1;key2=value2;...
     * @return stream或 string 或 map
     */
    public static <T> T doHttp(String apiUrl, Object params, String method, int type) {
        return doHttp(apiUrl, null, params, method, type);
    }

    /**
     * 发送 http 请求
     *
     * @param apiUrl
     *            API接口URL
     * @param headerMap
     *            header信息Map<String, String>,可设置cookie
     * @param params
     *            {Map<String, Object> K-V形式、json字符串}
     * @param method
     *            {null、或者post:POST请求、patch:PATCH请求、delete:DELETE请求、get:GET请求}
     * @param type
     *            {HttpUtil.RTN_TYPE_1:请求返回stream(此时流需要在外部手动关闭);HttpUtil.
     *            RTN_TYPE_2:string;HttpUtil.RTN_TYPE_3:返回一个map,map包含结果(
     *            结果是string形式)以及http状态码;HttpUtil.RTN_TYPE_4:返回一个map,map包含结果(
     *            结果是string形式), http状态码和cookie;其他情况返回string}
     *            如果结果是个map,key为:result,statusCode,cookie,分别返回 结果
     *            string,http状态码，cookie; cookie值键值对，格式
     *            key1=value1;key2=value2;...
     * @return stream或 string 或 map
     */
    public static <T> T doHttp(String apiUrl, Map<String, String> headerMap, Object params, String method, int type) {
        CloseableHttpClient httpClient = null;
        if (isSSL(apiUrl)) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                    .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        }
        else {
            httpClient = HttpClients.createDefault();
        }
        return doHttp(httpClient, apiUrl, headerMap, params, method, type);
    }

    /**
     * 发送 http 请求
     *
     * @param httpClient
     *            httpclient对象 由外部传入，用户 需要保持登录状态等情况 此时如果要ssl,那么要在外部加入ssl特性
     *            httpClient =
     *            HttpClients.custom().setSSLSocketFactory(HttpUtil.
     *            createSSLConnSocketFactory())
     *            .setConnectionManager(HttpUtil.getConnMgr()).
     *            setDefaultRequestConfig(HttpUtil..getRequestConfig()).build();
     * @param apiUrl
     *            API接口URL
     * @param headerMap
     *            header信息Map<String, String>,可设置cookie
     *
     * @param params
     *            {Map<String, Object> K-V形式、json字符串}
     * @param method
     *            {null、或者post:POST请求、patch:PATCH请求、delete:DELETE请求、get:GET请求}
     * @param type
     *            {HttpUtil.RTN_TYPE_1:请求返回stream(此时流需要在外部手动关闭);HttpUtil.
     *            RTN_TYPE_2:string;HttpUtil.RTN_TYPE_3:返回一个map,map包含结果(
     *            结果是string形式)以及http状态码;HttpUtil.RTN_TYPE_4:返回一个map,map包含结果(
     *            结果是string形式), http状态码和cookie;其他情况返回string}
     *            如果结果是个map,key为:result,statusCode,cookie,分别返回 结果
     *            string,http状态码，cookie; cookie值键值对，格式
     *            key1=value1;key2=value2;...
     * @return stream或 string 或 map
     */
    @SuppressWarnings("unchecked")
    public static <T> T doHttp(CloseableHttpClient httpClient, String apiUrl, Map<String, String> headerMap,
                               Object params, String method, int type) {
        HttpRequestBase httpPost = null;
        if (StringUtil.isNotBlank(method)) {
            if ("patch".equalsIgnoreCase(method)) {
                httpPost = new HttpPatch(apiUrl);
            }
            else if ("delete".equalsIgnoreCase(method)) {
                httpPost = new HttpDelete(apiUrl);
            }
            else if ("get".equalsIgnoreCase(method)) {
                httpPost = new HttpGet(apiUrl);
            }
            else if ("post".equalsIgnoreCase(method)) {
                httpPost = new HttpPost(apiUrl);
            }
        }
        else {
            httpPost = new HttpPost(apiUrl);
        }
        CloseableHttpResponse response = null;

        try {
            // 设置header信息
            if (headerMap != null && !headerMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            if (isSSL(apiUrl)) {
                httpPost.setConfig(requestConfig);
            }
            // 参数不为null、要处理参数
            if (params != null) {
                // get请求拼接在url后面
                if (httpPost instanceof HttpGet) {
                    StringBuffer param = new StringBuffer();
                    if (params instanceof Map) {
                        Map<String, Object> paramsConvert = (Map<String, Object>) params;
                        int i = 0;
                        for (String key : paramsConvert.keySet()) {
                            if (i == 0)
                                param.append("?");
                            else
                                param.append("&");
                            param.append(key).append("=").append(paramsConvert.get(key));
                            i++;
                        }
                    }
                    else {
                        param.append("?" + params.toString());
                    }
                    apiUrl += param;
                }
                // delete请求暂不处理
                else if (!(httpPost instanceof HttpDelete)) {
                    // K-V形式
                    if (params instanceof Map) {
                        Map<String, Object> paramsConvert = (Map<String, Object>) params;

                        List<NameValuePair> pairList = new ArrayList<>(paramsConvert.size());
                        for (Map.Entry<String, Object> entry : paramsConvert.entrySet()) {
                            NameValuePair pair = new BasicNameValuePair(entry.getKey(),
                                    entry.getValue() == null ? "" : entry.getValue().toString());
                            pairList.add(pair);
                        }
                        ((HttpEntityEnclosingRequestBase) httpPost)
                                .setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
                    }
                    // json格式
                    else {
                        StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");
                        stringEntity.setContentEncoding("UTF-8");
                        stringEntity.setContentType("application/json");
                        ((HttpEntityEnclosingRequestBase) httpPost).setEntity(stringEntity);
                    }
                }
            }
            response = httpClient.execute(httpPost);
            // int statusCode = response.getStatusLine().getStatusCode();
            // if (statusCode != HttpStatus.SC_OK) {
            // return null;
            // }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                if (type == RTN_TYPE_1) {
                    return (T) entity.getContent();
                }
                else if (RTN_TYPE_2 == type) {
                    return (T) EntityUtils.toString(entity, "UTF-8");
                }
                else if (RTN_TYPE_3 == type || RTN_TYPE_4 == type) {
                    Map<String, String> rtnMap = new HashMap<String, String>();
                    rtnMap.put("result", EntityUtils.toString(entity, "UTF-8"));
                    rtnMap.put("statusCode", response.getStatusLine().getStatusCode() + "");
                    if (RTN_TYPE_4 == type) {
                        rtnMap.put("cookie", getCookie(response));
                    }
                    return (T) rtnMap;
                }
                else {
                    return (T) EntityUtils.toString(entity, "UTF-8");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (response != null && type != RTN_TYPE_1) {
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 上传附件(post形式)
     *
     * @param url
     *            请求地址
     * @param headerMap
     *            header参数map Map<String, String>
     * @param paramMap
     *            额外的参数map,Map<String, String>
     * @param file
     *            可以选择本地文件上传；如果传了file，又传了fileName，那么文件名以fileName为准，否则 是file的文件名
     * @param fileName
     *            以流传输时，必须指定文件名
     * @param ssl
     *            是否需要ssl
     * @return result,返回上传结果，如果接口没有返回值，则为状态码
     */
    public static String upload(String url, Map<String, String> headerMap, Map<String, String> paramMap, File file,
                                String fileName, boolean ssl) {
        return upload(url, headerMap, paramMap, file, null, fileName, ssl);
    }

    /**
     * 上传附件(post形式)
     *
     * @param url
     *            请求地址
     * @param headerMap
     *            header参数map Map<String, String>
     * @param paramMap
     *            额外的参数map,Map<String, String>
     * @param in
     *            文件流
     * @param fileName
     *            以流传输时，必须指定文件名
     * @param ssl
     *            是否需要ssl
     * @return result,返回上传结果，如果接口没有返回值，则为状态码
     */
    public static String upload(String url, Map<String, String> headerMap, Map<String, String> paramMap, InputStream in,
                                String fileName, boolean ssl) {
        return upload(url, headerMap, paramMap, null, in, fileName, ssl);
    }

    /**
     * 上传附件(post形式)
     *
     * @param httpClient
     *            外部传入httpClient
     * @param url
     *            请求地址
     * @param headerMap
     *            header参数map Map<String, String>
     * @param paramMap
     *            额外的参数map,Map<String, String>
     * @param file
     *            可以选择本地文件上传；如果传了file，又传了fileName，那么文件名以fileName为准，否则 是file的文件名
     * @param fileName
     *            以流传输时，必须指定文件名
     * @param ssl
     *            是否需要ssl
     * @return result,返回上传结果，如果接口没有返回值，则为状态码
     */
    public static String upload(CloseableHttpClient httpClient, String url, Map<String, String> headerMap,
                                Map<String, String> paramMap, File file, String fileName, boolean ssl) {
        return upload(httpClient, url, headerMap, paramMap, file, null, fileName, ssl);
    }

    /**
     * 上传附件(post形式)
     *
     * @param httpClient
     *            外部传入httpClient
     * @param url
     *            请求地址
     * @param headerMap
     *            header参数map Map<String, String>
     * @param paramMap
     *            额外的参数map,Map<String, String>
     * @param in
     *            文件流
     * @param fileName
     *            以流传输时，必须指定文件名
     * @param ssl
     *            是否需要ssl
     * @return result,返回上传结果，如果接口没有返回值，则为状态码
     */
    public static String upload(CloseableHttpClient httpClient, String url, Map<String, String> headerMap,
                                Map<String, String> paramMap, InputStream in, String fileName, boolean ssl) {
        return upload(httpClient, url, headerMap, paramMap, null, in, fileName, ssl);
    }

    /**
     * 上传附件(post形式)
     *
     * @param url
     *            请求地址
     * @param headerMap
     *            header参数map Map<String, String>
     * @param paramMap
     *            额外的参数map,Map<String, String>
     * @param file
     *            可以选择本地文件上传，file,in互斥；如果传了file，又传了fileName，那么文件名以fileName为准，否则
     *            是file的文件名
     * @param in
     *            文件流
     * @param fileName
     *            以流传输时，必须指定文件名
     * @param ssl
     *            是否需要ssl
     * @return result,返回上传结果，如果接口没有返回值，则为状态码
     */
    private static String upload(String url, Map<String, String> headerMap, Map<String, String> paramMap, File file,
                                 InputStream in, String fileName, boolean ssl) {
        CloseableHttpClient httpClient = null;
        if (ssl) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                    .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        }
        else {
            httpClient = HttpClients.createDefault();
        }
        return upload(httpClient, url, headerMap, paramMap, file, in, fileName, ssl);
    }

    /**
     * 上传附件(post形式)
     *
     * @param httpClient
     *            外部传入httpClient
     * @param url
     *            请求地址
     * @param headerMap
     *            header参数map Map<String, String>
     * @param paramMap
     *            额外的参数map,Map<String, String>
     * @param file
     *            可以选择本地文件上传，file,in互斥；如果传了file，又传了fileName，那么文件名以fileName为准，否则
     *            是file的文件名
     * @param in
     *            文件流
     * @param fileName
     *            以流传输时，必须指定文件名
     * @param ssl
     *            是否需要ssl
     * @return result,返回上传结果，如果接口没有返回值，则为状态码
     */
    private static String upload(CloseableHttpClient httpClient, String url, Map<String, String> headerMap,
                                 Map<String, String> paramMap, File file, InputStream in, String fileName, boolean ssl) {
        String result = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            // 设置header信息
            if (headerMap != null && !headerMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            if (ssl) {
                httpPost.setConfig(requestConfig);
            }
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // 选择以file形式上传
            if (file != null && file.exists()) {
                if (StringUtil.isNotBlank(fileName)) {
                    builder.addBinaryBody(DEFAULT_BINARYBODY_KEYNAME, file, ContentType.DEFAULT_BINARY, fileName);
                }
                else {
                    builder.addBinaryBody(DEFAULT_BINARYBODY_KEYNAME, file);
                }
            }
            // 以流上传
            else if (in != null && StringUtil.isNotBlank(fileName)) {
                builder.addBinaryBody(DEFAULT_BINARYBODY_KEYNAME, in, ContentType.DEFAULT_BINARY, fileName);
            }
            if (paramMap != null && !paramMap.isEmpty()) {
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.TEXT_PLAIN));
                }
            }
            HttpEntity reqEntity = builder.build();
            httpPost.setEntity(reqEntity);

            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            else {
                result = response.getStatusLine().getStatusCode() + "";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String getCookie(HttpResponse httpResponse) {
        Map<String, String> cookieMap = new HashMap<String, String>(64);
        Header headers[] = httpResponse.getHeaders("Set-Cookie");
        if (headers == null || headers.length == 0) {
            return null;
        }
        String cookie = "";
        for (int i = 0; i < headers.length; i++) {
            cookie += headers[i].getValue();
            if (i != headers.length - 1) {
                cookie += ";";
            }
        }
        String cookies[] = cookie.split(";");
        for (String c : cookies) {
            c = c.trim();
            if (cookieMap.containsKey(c.split("=")[0])) {
                cookieMap.remove(c.split("=")[0]);
            }
            cookieMap.put(c.split("=")[0],
                    c.split("=").length == 1 ? "" : (c.split("=").length == 2 ? c.split("=")[1] : c.split("=", 2)[1]));
        }
        String cookiesTmp = "";
        for (String key : cookieMap.keySet()) {
            cookiesTmp += key + "=" + cookieMap.get(key) + ";";
        }
        return cookiesTmp.substring(0, cookiesTmp.length() - 2);
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    public static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
            {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier()
            {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        }
        catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

    public static PoolingHttpClientConnectionManager getConnMgr() {
        return connMgr;
    }

    public static RequestConfig getRequestConfig() {
        return requestConfig;
    }

    private static boolean isSSL(String apiUrl) {
        if (apiUrl.indexOf("https") != -1 ) {
            return true;
        }
        else {
            return false;
        }
    }

}
