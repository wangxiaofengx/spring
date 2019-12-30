package com.mrwang.config;

import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class CustomBanner implements org.springframework.boot.Banner {
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        out.println(" ......................我佛慈悲......................");
        out.println("                       _oo0oo_                      ");
        out.println("                      o8888888o                     ");
        out.println("                     88\" . \"88                     ");
        out.println("                      (| -_- |)                     ");
        out.println("                     0\\  =  /0                     ");
        out.println("                    ___/‘---’\\___                   ");
        out.println("                  .' \\|       |/ '.                 ");
        out.println("                 / \\\\|||  :  |||// \\                ");
        out.println("                / _||||| -卍-|||||_ \\               ");
        out.println("               |   | \\\\\\  -  /// |   |              ");
        out.println("               | \\_|  ''\\---/''  |_/ |              ");
        out.println("               \\  .-\\__  '-'  ___/-. /              ");
        out.println("             ___'. .'  /--.--\\  '. .'___            ");
        out.println("          .\"\" ‘<  ‘.___\\_<|>_/___.’ >’ \"\".          ");
        out.println("         | | :  ‘- \\‘.;‘\\ _ /’;.’/ - ’ : | |        ");
        out.println("         \\  \\ ‘_.   \\_ __\\ /__ _/   .-’ /  /        ");
        out.println("     =====‘-.____‘.___ \\_____/___.-’___.-’=====     ");
        out.println("                       ‘=---=’                      ");
        out.println("                                                    ");
        out.println("....................佛祖开光 ,永无BUG...................");
    }
}
