package cn.sdkd.ccse.jsqles.chromeapp.host;

import cn.sdkd.ccse.jsqles.chromeapp.msgprocessor.ChromeAppMsgProcessor;
import cn.sdkd.ccse.jsqles.chromeapp.msgprocessor.ChromeAppResponseMessage;

import java.io.*;

/** 为chrome扩展程序应用通信的host程序
 * Created by sam on 2019/8/3.
 */
public class ChromeAppHost {
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
        FileWriter fileWritter = null;
        InputStream in = System.in;
        byte[] msg_length = new byte[4];
        try {

            fileWritter = new FileWriter("c:\\log.txt",true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write("started... ");
            bufferWritter.newLine();

            in.read(msg_length, 0, 4);

            int length = bytesToInt(msg_length);

            bufferWritter.write("length: " + length );
            bufferWritter.newLine();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++)
            {
                    sb.append((char)in.read());
            }
            /*获得消息*/
            String msg = sb.toString();

            bufferWritter.write("msg: " + msg);
            bufferWritter.newLine();

            ChromeAppMsgProcessor chromeAppMsgProcessor = new ChromeAppMsgProcessor(msg);
            ChromeAppResponseMessage chromeAppResponseMessage = chromeAppMsgProcessor.process();
            sendMsg(chromeAppResponseMessage.getJsonMsgText());


            bufferWritter.write("msg sended. " + msg);
            bufferWritter.newLine();
            bufferWritter.close();


        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }
}
