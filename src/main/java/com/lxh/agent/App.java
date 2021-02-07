package com.lxh.agent;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class App {

    public static void main(String[] args) {
        System.out.println("这是测试用的主类：com.lxh.agent.App");
        log.info("==> start operation...");
        longOperation();
        log.info("==> end operation...");
    }

    @SneakyThrows
    public static void longOperation() {
        Thread.sleep(1000);
    }
}
