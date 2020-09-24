package com.example.mymobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by JW.S on 2020/9/22 8:30 PM.
 */
public class MD5Utils {
    /**
     * 获取文件的md5指纹
     * 方式一 : 通过传入文件的方式
     * @param file
     * @return
     */
    public static String md5(File file) {
        FileInputStream in = null;
        try {
            //MD5
            in = new FileInputStream(file);
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8129];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            //文件指纹
            byte[] digest = digester.digest();
            //转换byte -->String
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                int c = b & 0xff;//16进制的数据
                // 0,1,2....9,A,B,C,D,E,F
                String str = Integer.toHexString(c);
                if (str.length() == 1) {
                    str = 0 + str;
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(in);
        }
        return null;
    }

    /**
     * 方式二：通过传入输入流的方式
     * @param in
     * @return
     */
    public static String md5(InputStream in) {
        try {
            //MD5
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int len = 0;
            while ((len = in.read(bytes)) > 0) {
                digester.update(bytes, 0, len);
            }
            //文件指纹
            byte[] digest = digester.digest();
            // 转换byte --》String
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                int c = b & 0xff;//16进制的数据
                // 0,1,2....9,A,B,C,D,E,F
                String str = Integer.toHexString(c);
                if (str.length() == 1) {
                    str = 0 + str;
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeIO(in);
        }
        return null;
    }
}
