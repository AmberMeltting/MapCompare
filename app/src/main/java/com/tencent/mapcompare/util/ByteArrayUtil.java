package com.tencent.mapcompare.util;

/**
 * Created by wangxiaokun on 16/5/12.
 */
public class ByteArrayUtil {
    public static byte[] byteMerge(byte[] byte1, byte[] byte2) {
        byte[] bytes = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, bytes, 0, byte1.length);
        System.arraycopy(byte2, 0, bytes, byte1.length, byte2.length);
        return bytes;
    }
}
