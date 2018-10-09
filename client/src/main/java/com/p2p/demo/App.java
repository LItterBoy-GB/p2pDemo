package com.p2p.demo;

import com.p2p.demo.core.Client;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        if(args.length!=2){
            System.out.println("执行命令 client.jar ip port");
        }
        new Client(args[0],Integer.valueOf(args[1])).contect();
    }
}
