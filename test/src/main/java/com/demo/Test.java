package com.demo;

import java.io.IOException;
import java.net.URL;

public class Test {
    public static void main(String[] args) throws IOException {
        String host = "http://www.gdqywx.com/";

        URL url = new URL(host);
        System.out.println(url.getPath());
        System.out.println(host.replace(url.getPath(), ""));
        System.out.println(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort());
    }
}
