package cn.sdkd.ccse.jsqles.chromeapp.host;

import cn.sdkd.ccse.jsqles.chromeapp.msgprocessor.ChromeAppMsgProcessor;
import cn.sdkd.ccse.jsqles.chromeapp.msgprocessor.ChromeAppResponseMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.SQLException;

/** 为chrome扩展程序应用通信的host程序
 * Created by sam on 2019/8/3.
 */
public class ChromeAppHost {
    protected static Logger logger = LogManager.getLogger(ChromeAppHost.class);

    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    public static int bytesToInt(byte[] bytes)
    {

        int a = bytes[0] & 0xFF;
        a += ((bytes[1] << 8) & 0xFF00);
        a += ((bytes[2] << 16) & 0xFF0000);
        a += ((bytes[3] << 24) & 0xFF000000);

        return a;

    }

    /*发送消息*/
    public static void sendMsg(String message) throws IOException {
        int len = message.length();
        System.out.write((byte)((len>>0) & 0xFF));
        System.out.write((byte)((len>>8) & 0xFF));
        System.out.write((byte)((len>>16) & 0xFF));
        System.out.write((byte)((len>>24) & 0xFF));
        System.out.write(message.getBytes());
        System.out.flush();
    }

    /*工作模式：接收消息-->处理消息-->返回消息*/
    public static void  main(String[] args){
        String response =  "{\"success\":false}";
        InputStream in = System.in;
        byte[] msg_length = new byte[4];
        try {

//            logger.info("started... ");

            in.read(msg_length, 0, 4);

            int length = bytesToInt(msg_length);
//            logger.info("length: " + length);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++)
            {
                    sb.append((char)in.read());
            }
            /*获得消息*/
            String msg = sb.toString();
//            logger.info("msg: " + msg);

            ChromeAppMsgProcessor chromeAppMsgProcessor = new ChromeAppMsgProcessor(msg);
            String respmsg = chromeAppMsgProcessor.process();
            sendMsg(respmsg);
            logger.info("msg sended:" + respmsg);

        } catch (IOException e) {
            try {
                logger.error(e);
                sendMsg(response);
            } catch (IOException e1) {
                logger.error(e1);
            }
        } catch (SQLException e) {
            try {
                logger.error(e);
                sendMsg(response);
            } catch (IOException e1) {
                logger.error(e1);
            }
        } catch (ClassNotFoundException e) {
            try {
                logger.error(e);
                sendMsg(response);
            } catch (IOException e1) {
                logger.error(e1);
            }
        }

    }
}
