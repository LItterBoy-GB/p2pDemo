package com.p2p.demo.handle;

import com.p2p.demo.constant.Command;
import com.p2p.demo.pojo.Result;
import com.p2p.demo.util.ByteUtil;
import com.p2p.demo.util.JSONUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @program: p2pDemo
 * @description: 处理客户端业务线程
 * @author: JavaLitterBoy
 * @create: 2018-10-08 14:59
 **/
public class Handle implements Runnable {

    private Map<String, String[]> users;
    private Socket socket;
    private String user_name;
    private InputStream in;
    private OutputStream out;

    public Handle(Socket socket) throws IOException {
        this.socket = socket;
        this.users = new HashMap<>();
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);
        byte[] data;
        String res;
        Result result;
        String[] ip_port;
        while (socket.isConnected()) {            // 只要连接未断开  则持续处理客户端事务
            String line = scan.nextLine();
            String arr[] = line.split(" ");         // 命令格式  command 参数* 空格间隔
            try {
                switch (arr[0].toLowerCase()) {
                    case Command.HELP:          // 帮助菜单
                        System.out.println("help 查看帮助");
                        System.out.println("hello 向服务器表明身份 hello your name");
                        System.out.println("list 向服务器获取在线用户");
                        System.out.println("send 向指定用户发送消息 send who msg");
                        break;
                    case Command.SEND:     // 向某个目标发送消息 udp连接
                        if (arr.length < 3) {
                            System.out.println("命令无效! send rose I LOVE YOU");
                            continue;
                        } else {
                            ip_port = users.get(arr[1]);
                            if (ip_port == null) {
                                System.out.println("先从服务器获取用户信息!");
                                continue;
                            }
                            if (this.user_name == null) {
                                System.out.println("先表明身份! hello jack");
                                continue;
                            }
                            StringBuffer send_data = new StringBuffer(this.user_name);
                            send_data.append(" : ");
                            for (int i = 2; i < arr.length; i++) {
                                send_data.append(arr[i]);
                                send_data.append(" ");
                            }
                            // udp 发送消息
                            udp_send_data(ip_port[0], Integer.valueOf(ip_port[1]), send_data.toString().getBytes());
                        }
                        break;
                    case Command.HELLO:         // 表明身份
                        if (arr.length != 2) {
                            System.out.println("命令无效! hello who");
                            continue;
                        }
                        if(this.user_name!=null){
                            System.out.println("身份已经表明!"+this.user_name);
                            continue;
                        }
                        this.user_name = arr[1];
                        send_data(line.getBytes());
                        data = read_data();
                        res = new String(data);
                        result = JSONUtils.jsonToBean(res, Result.class);
                        if (result == null) {
                            System.out.println("返回数据错误");
                        } else {
                            System.out.println(result.getMessage());
                        }
                        break;
                    case Command.LIST:
                        send_data(line.getBytes());
                        data = read_data();
                        res = new String(data);
                        result = JSONUtils.jsonToBean(res, Result.class);
                        if (result == null || 200 != result.getCode()) {
                            if (result == null) {
                                System.out.println("返回数据错误");
                            } else {
                                System.out.println(result.getMessage());
                            }
                        } else {
                            users.clear();
                            String[] user_arr = result.getData().split(",");
                            for (String user : user_arr) {
                                String[] u = user.split(";");
                                ip_port = new String[2];
                                ip_port[0] = u[1];
                                ip_port[1] = u[2];
                                users.put(u[0], ip_port);
                                System.out.println(user);
                            }
                        }
                        break;
                    default:
                        send_data(line.getBytes());
                        data = read_data();
                        System.out.println(new String(data));
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void send_data(byte[] data) throws IOException {
        out.write(ByteUtil.intToByteArray(data.length));
        out.write(data);
        out.flush();
    }

    public byte[] read_data() throws IOException {
        byte[] len_arr = new byte[4];
        // 读取数据长度
        in.read(len_arr, 0, 4);
        int data_len = ByteUtil.byteArrayToInt(len_arr);
        byte[] data = new byte[data_len];
        // 读取数据
        in.read(data);
        return data;
    }

    public void udp_send_data(String ip, int port, byte data[]) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        // 先发内容长度
        DatagramPacket datagramPacket = new DatagramPacket(ByteUtil.intToByteArray(data.length), 4, InetAddress.getByName(ip), port);
        datagramSocket.send(datagramPacket);

        // 数据内容
        datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
        datagramSocket.send(datagramPacket);
    }

}
