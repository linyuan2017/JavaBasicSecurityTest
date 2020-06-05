package com.example.digitalsignature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyDigest {




    public void testDigest() {
        try {
            String myInfo = "我的测试信息";
            MessageDigest alg = MessageDigest.getInstance("SHA-1");
            alg.update(myInfo.getBytes());
            byte[] digest = alg.digest();
            System.out.println("本信息摘要是:" + byte2hex(digest));

            // 通过某中方式传给其他人你的信息(myinfo)和摘要(digest) 对方可以判断是否更改或传输正常
            MessageDigest algb = MessageDigest.getInstance("SHA-1");
            algb.update(myInfo.getBytes());
            if (algb.isEqual(digest, algb.digest())) {
                System.out.println("信息检查正常");
            } else {
                System.out.println("摘要不相同");
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("非法摘要算法");
        }
    }

    public String byte2hex(byte[] b) { // 二进制转字符串
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs + ":";
            }
        }
        return hs.toLowerCase();

    }
}
