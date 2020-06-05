package com.example.digitalsignature;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class TestDES {

    public void run() {
        // 添加新安全算法, 如果用JCE就要把它添加进去
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        String Algorithm = "DES"; // 定义 加密算法, 可用DES, DESede,Blowfish
        String myinfo = "要加密的信息";
        try {
            // 生成密钥
            KeyGenerator kengen = KeyGenerator.getInstance(Algorithm);
            SecretKey deskey = kengen.generateKey();
            // 加密
            System.out.println("加密前的二进制串:" + byte2hex(myinfo.getBytes()));
            System.out.println("加密前的信息:" + myinfo);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] cipherByte = c1.doFinal(myinfo.getBytes());
            System.out.println("加密后的二进制串: " + byte2hex(cipherByte));
            // 解密
            c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            byte[] clearByte = c1.doFinal(cipherByte);
            System.out.println("解密后的二进制串:" + byte2hex(cipherByte));
            System.out.println("解密后的信息: " + (new String(clearByte)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
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
