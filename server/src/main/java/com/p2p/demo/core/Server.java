package com.p2p.demo.core;

import com.p2p.demo.handle.Handle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: p2pDemo
 * @description:
 * @author: JavaLitterBoy
 * @create: 2018-10-08 14:40
 **/
public class Server {
    private int port;
    private ServerSocket server;

    public Server() throws IOException {
        this.port = 1124;
        this.server = new ServerSocket(this.port);
    }

    public Server(int port) throws IOException {
        this.port = port;
        this.server = new ServerSocket(this.port);
    }

    public void start() throws IOException {
        while(true){
            Socket socket = server.accept();
            System.out.println(socket.getInetAddress().getHostAddress()+":"+String.valueOf(socket.getPort())+" 连接成功.");
            new Thread(new Handle(socket)).start();     // 开启线程处理业务
        }
    }
}
