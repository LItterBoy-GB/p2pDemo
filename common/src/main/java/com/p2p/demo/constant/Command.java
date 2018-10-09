package com.p2p.demo.constant;

/**
 * @program: p2pDemo
 * @description: 协议相关命令
 * @author: JavaLitterBoy
 * @create: 2018-10-08 15:44
 **/
public class Command {
    public final static String HELLO = "hello";            // 向服务器表明身份 hello jack
    public final static String HEART = "heart";            // 向服务器发送心跳
    public final static String PASV = "pasv";              // 客户端A 要求服务器 让其它客户端B被动连接自己  pasv rose(客户端B)
    public final static String LIST = "list";              // 向服务器索要当前在线用户
    public final static String SEND = "send";              // 向指定客户端发送消息 send rose xxxxxxxxxx
    public final static String HELP = "help";              // 向指定客户端发送消息 send rose xxxxxxxxxx
}
