package com.p2p.demo.handle;

import com.p2p.demo.core.Server;
import com.p2p.demo.pojo.Result;
import com.p2p.demo.util.ByteUtil;
import com.p2p.demo.constant.Command;
import com.p2p.demo.util.JSONUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @program: p2pDemo
 * @description: 处理客户端业务线程
 * @author: JavaLitterBoy
 * @create: 2018-10-08 14:59
 **/
public class Handle implements Runnable {

    public static Map<String,Socket> users = new HashMap<>();

    private Socket socket;
    private String user_name;
    private InputStream in;
    private OutputStream out;

    public Handle(Socket socket) throws IOException {
        this.socket = socket;
        this.user_name = null;
        in = this.socket.getInputStream();
        out = this.socket.getOutputStream();
    }

    @Override
    public void run() {
        while(socket.isConnected()){            // 只要连接未断开  则持续处理客户端事务
            // 先获取本次数据长度 4个字节
            StringBuffer result=null;
            int data_len;
            byte temp[];
            byte len_arr[] = new byte[4];
            try {
                if(in.read(len_arr,0,4)<4){
                    System.out.println(socket.getInetAddress().getHostAddress()+":"+String.valueOf(socket.getPort())+" 数据格式错误 4个字节(表明内容长度)+内容");
                    result = new StringBuffer(JSONUtils.beanToJson(Result.error("数据格式错误 4个字节(表明内容长度)+内容",null)));
                }else{
                    data_len = ByteUtil.byteArrayToInt(len_arr);
                    byte data[] = new byte[data_len];
                    if(in.read(data)<data_len){
                        System.out.println(socket.getInetAddress().getHostAddress() + ":" + String.valueOf(socket.getPort()) + " 数据格式不完整 4个字节(表明内容长度)+内容");
                        result = new StringBuffer(JSONUtils.beanToJson(Result.error("数据格式不完整 4个字节(表明内容长度)+内容",null)));
                    }else{
                        String line = new String(data);
                        System.out.println(socket.getInetAddress().getHostAddress() + ":" + String.valueOf(socket.getPort()) + " "+line);
                        String arr[] = line.split(" ");         // 命令格式  command 参数* 空格间隔
                        switch (arr[0].toLowerCase()){
                            case Command.HEART:
                                break;       // 心跳不需要处理
                            case Command.HELLO:     // 表明身份  除了心跳意外其余动作必须先表明身份
                                Socket temp_socket = users.get(arr[1]);
                                if(temp_socket==null) {
                                    synchronized (users) {
                                        this.user_name = arr[1];
                                        users.put(arr[1], socket);
                                        result = new StringBuffer(JSONUtils.beanToJson(Result.ok("hello server",null)));
                                    }
                                }else{
                                    result = new StringBuffer(JSONUtils.beanToJson(Result.error(arr[1]+" 已经被占用",null)));
                                }
                                break;
                            case Command.LIST:
                                if(this.user_name==null){
                                    result = new StringBuffer(JSONUtils.beanToJson(Result.error("请先表明身份 hello java(your name)",null)));
                                }else{
                                    Set<String> user_names =  users.keySet();
                                    result = new StringBuffer();
                                    for(String name:user_names){
                                        result.append(name);
                                        result.append(";");
                                        result.append(users.get(name).getInetAddress().getHostAddress());
                                        result.append(";");
                                        result.append(users.get(name).getPort());
                                        result.append(",");
                                    }
                                    result = new StringBuffer(JSONUtils.beanToJson(Result.ok("获取成功",result.toString())));
                                }
                                break;
                            case Command.PASV:          // 暂时先不做实现 主要用于 由于不支持NAT穿透时导致连接失败 尝试让对方连接自己
                                if(this.user_name==null){
                                    result = new StringBuffer(JSONUtils.beanToJson(Result.error("请先表明身份 hello java(your name)",null)));
                                }else {
                                    result = new StringBuffer(JSONUtils.beanToJson(Result.error("暂时先不做实现",null)));
                                }
                                break;
                            default:
                                result = new StringBuffer(JSONUtils.beanToJson(Result.error("命令错误",null)));
                                break;
                        }
                    }
                }
                if(result!=null) {
                    System.out.println(socket.getInetAddress().getHostAddress() + ":" + String.valueOf(socket.getPort()) + " "+result.toString());
                    temp = result.toString().getBytes();
                    out.write(ByteUtil.intToByteArray(temp.length));
                    out.write(temp);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
