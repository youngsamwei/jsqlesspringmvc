package cn.sdkd.ccse.jsqles.chromeapp;

import java.io.UnsupportedEncodingException;

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
}
