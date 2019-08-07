package cn.sdkd.ccse.jsqles.chromeapp;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sam on 2019/8/6.
 */
public class Utils {
    public static String getEncoding(String str){

        String encoding = "UTF-8";

        try {

            if (str.equals(new String(str.getBytes(),encoding))) {

                return encoding; }

        } catch (UnsupportedEncodingException e) {

// TODO Auto-generated catch block e.printStackTrace();

        }

        encoding = "GBK";

        try {

            if (str.equals(new String(str.getBytes(),encoding))) {

                return encoding; } } catch (UnsupportedEncodingException e) {

// TODO Auto-generated catch block e.printStackTrace();

        }

        encoding = "ISO-8859-1";

        try {

            if (str.equals(new String(str.getBytes(),encoding))) {

                return encoding;

            }

        } catch (UnsupportedEncodingException e) {

// TODO Auto-generated catch block e.printStackTrace();

        }

        encoding = "GB2312";

        try {

            if (str.equals(new String(str.getBytes(),encoding))) {

                return encoding;

            }

        } catch (UnsupportedEncodingException e) {

// TODO Auto-generated catch block e.printStackTrace();

        }

        return null;

    }

    public static String convertEncoding_Str(String src,String srcCharset,String destCharet)
            throws UnsupportedEncodingException{
        byte[] bts = src.getBytes(destCharet);
        return new String(bts, destCharet);
    }

    public static byte[] getUTF8BytesFromGBKString(String gbkStr) {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }

    private static StringUtil chinese(String chinese){
        StringUtil util = new StringUtil();
        char[]chars = chinese.toCharArray() ;
        int index = 0 ;
        StringBuffer buffer = new StringBuffer();
        for(char c : chars){
            String temp = String.valueOf(c) ;
            if(temp.getBytes().length == 1){
                util.map.put( index , temp ) ;
            }else{
                buffer.append( temp );
            }
            index++;
        }
        util.chinese = buffer.toString() ;
        return util ;
    }

    public static String gbk2utf8(String chenese) {
        StringUtil strUtil = chinese( chenese ) ;
        char c[] = strUtil.chinese.toCharArray( ) ;
        byte[] fullByte = new byte[3 * c.length];
        for (int i = 0; i < c.length; i++) {
            int m = (int) c[i];
            String word = Integer.toBinaryString(m);

            StringBuffer sb = new StringBuffer();
            int len = 16 - word.length();
            for (int j = 0; j < len; j++) {
                sb.append("0");
            }
            sb.append(word);
            sb.insert(0, "1110");
            sb.insert(8, "10");
            sb.insert(16, "10");

            String s1 = sb.substring(0, 8);
            String s2 = sb.substring(8, 16);
            String s3 = sb.substring(16);

            byte b0 = Integer.valueOf(s1, 2).byteValue();
            byte b1 = Integer.valueOf(s2, 2).byteValue();
            byte b2 = Integer.valueOf(s3, 2).byteValue();
            byte[] bf = new byte[3];
            bf[0] = b0;
            fullByte[i * 3] = bf[0];
            bf[1] = b1;
            fullByte[i * 3 + 1] = bf[1];
            bf[2] = b2;
            fullByte[i * 3 + 2] = bf[2];
        }
        String reutrnStr = null ;
        try {
            reutrnStr = new String(fullByte, "UTF-8");
        } catch (Exception e) {
        }
        StringBuffer returnBuffer = new StringBuffer(  reutrnStr );
        for(Map.Entry<Integer, String> entry : strUtil.map.entrySet()){
            returnBuffer.insert(entry.getKey() , entry.getValue() ) ;
        }

        return returnBuffer.toString() ;
    }
}
class StringUtil{
    public String chinese ;

    public Map<Integer , String> map = new HashMap<Integer, String>();
}