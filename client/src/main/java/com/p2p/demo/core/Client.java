package com.p2p.demo.core;

import com.p2p.demo.handle.Handle;
import com.p2p.demo.handle.ReciveHandle;
import com.p2p.demo.util.ByteUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @program: p2pDemo
 * @description:
 * @author: JavaLitterBoy
 * @create: 2018-10-08 16:33
 **/
public class Client {
    private String ip;
    private int port;
    private Socket socket;

    public Client(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void contect() throws IOException {
        socket = new Socket(ip,port);
        // 心跳定时器  0.1s 一次
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (socket){

                    String heart = "heart";
                    try {
                        socket.getOutputStream().write(ByteUtil.intToByteArray(heart.getBytes().length));
                        socket.getOutputStream().write(heart.getBytes());
                        socket.getOutputStream().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        },0,10000);

        // 业务线程
        new Thread(new Handle(socket)).start();

        // 消息接收
        System.out.println("服务器端口:"+String.valueOf(socket.getPort()));

        System.out.println("本地端口:"+String.valueOf(socket.getLocalPort()));
        new Thread(new ReciveHandle(socket.getLocalPort())).start();
    }
}
