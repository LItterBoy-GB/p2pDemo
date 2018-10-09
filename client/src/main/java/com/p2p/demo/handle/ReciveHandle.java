package com.p2p.demo.handle;

import com.p2p.demo.util.ByteUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @program: p2pDemo
 * @description: udp接收信息线程
 * @author: JavaLitterBoy
 * @create: 2018-10-09 08:47
 **/
public class ReciveHandle implements Runnable {

    private int port;
    private DatagramSocket datagramSocket;

    public ReciveHandle(int port) throws SocketException {
        this.port = port;
        datagramSocket = new DatagramSocket(this.port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] len_arr = new byte[4];
                DatagramPacket datagramPacket = new DatagramPacket(len_arr, 4);
                datagramSocket.receive(datagramPacket);
                int len = ByteUtil.byteArrayToInt(len_arr);

                byte[] data = new byte[len];
                datagramPacket = new DatagramPacket(data, len);
                datagramSocket.receive(datagramPacket);
                System.out.println(new String(data, 0, datagramPacket.getLength()));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
