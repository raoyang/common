package com.game.match;

import com.game.match.domain.MatchMessage;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class MatchTest {
    public static void main(String args[]){
        testSendMatchReq();
    }

    private static void testSendMatchReq(){
        Socket socket = null;
        OutputStream ops = null;
        try{
            socket = new Socket("127.0.0.1", 8003);
            ops = socket.getOutputStream();

            MatchMessage matchMessage = new MatchMessage();
            matchMessage.setGameId("10115531508830001003");
            for (int i = 0; i < 3; i ++) {
                ByteBuf buf = Unpooled.buffer(64);
                buf.writeShort(1);
                buf.writeShort(1);
                int accountId = 10000001 + i;
                matchMessage.setAccountId(accountId);
                String msg = new Gson().toJson(matchMessage);
                byte[] bytes = msg.getBytes(Charset.forName("utf-8"));
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
                byte[] b = new byte[buf.readableBytes()];
                buf.readBytes(b);
                ops.write(b);

                Thread.sleep(10);
            }

            /*MatchMessage matchMessage = new MatchMessage();
            matchMessage.setGameId("10115531508830001003");
            matchMessage.setAccountId(10000001);
            matchMessage.setRoomId("1556518472027a0ccb1b5c20e9b85a4df86c522050f3");
            ByteBuf buf = Unpooled.buffer(64);
            buf.writeShort(1);
            buf.writeShort(3);
            String msg = new Gson().toJson(matchMessage);
            byte[] bytes = msg.getBytes(Charset.forName("utf-8"));
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
            byte[] b = new byte[buf.readableBytes()];
            buf.readBytes(b);
            ops.write(b);

            Thread.sleep(10);*/


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(ops != null){
                try{
                    ops.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(socket != null){
                try{
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
