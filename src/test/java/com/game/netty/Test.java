package com.game.netty;

import com.game.netty.message.BaseMessage;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {

    public static void main(String args[])  {
        List<NettyClient> list = new ArrayList<>();
        for(int i = 0 ; i < 50 ; i ++){
            try{
                NettyClient client = new NettyClient("127.0.0.1", 8003);
                client.start();
                list.add(client);
                System.out.println("新增一条客户端链接...");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println("开始启动线程...");
        for(int i = 0 ; i < 50; i ++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try{
                            NettyClient client = list.get(new Random().nextInt(50));
                            BaseMessage msg = BaseMessage.valueOf((short)3,(short)1);
                            msg.setBody("hello,world".getBytes(Charset.forName("utf-8")));
                            client.getChannel().writeAndFlush(msg);
                            Thread.sleep(500);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }
}
