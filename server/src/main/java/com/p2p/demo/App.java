package com.p2p.demo;

import com.p2p.demo.core.Server;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("可自定义端口 server.jar port");
        if(args.length==0)
            new Server().start();
        else
            new Server(Integer.valueOf(args[0])).start();
    }
}
