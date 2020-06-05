package com.example.digitalsignature;


import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class TestDHKey {

    public void run() throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        System.out.println("Alice 产生 DH 对...");
        KeyPairGenerator aliceKPairGen = KeyPairGenerator.getInstance("DH");
        aliceKPairGen.initialize(512);
        KeyPair aliceKPair = aliceKPairGen.generateKeyPair(); // 生成时间长
        // Alice 生成公共密钥 alicePubKeyEnc 并发送给 Bob
        // 比如用文件方式, socket ...
        byte[] alicePubKeyEnc = aliceKPair.getPublic().getEncoded();
        // bob 接收到 alice 的编码后的公钥, 将其解码
        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(alicePubKeyEnc);
        PublicKey alicePubKey = bobKeyFac.generatePublic(x509EncodedKeySpec);
        System.out.println("alice 公钥 Bob 解码成功");
        // bob 必须用相同的参数初始化他的DH key 对, 所以要从alice发给他的公开密钥中
        // 读出参数, 再用这个参数初始化他的DH key 对
        // 从alicePubKey中取alice初始化时用的DH key 对
        DHParameterSpec dhParameterSpec = ((DHPublicKey)alicePubKey).getParams();
        KeyPairGenerator bobKPairGen = KeyPairGenerator.getInstance("DH");
        bobKPairGen.initialize(dhParameterSpec);
        KeyPair bobKPair = bobKPairGen.generateKeyPair();
        System.out.println("bob: 生成 DH key 对 成功");

        KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
        bobKeyAgree.init(bobKPair.getPrivate());
        System.out.println("bob : 初始化本地 key 成功");
        // bob 生成本地的密钥 bobDESKey
        bobKeyAgree.doPhase(alicePubKey, true);
        SecretKey bobDESKey = bobKeyAgree.generateSecret("DES");
        System.out.println("bob 用 alice 的公钥定位本地key,生成本地DES 密钥成功");
        // bob 生成公共密钥 bobPubKeyEnc 并发送给alice
        byte[] bobPubKeyEnc = bobKPair.getPublic().getEncoded();
        System.out.println("bob 向Alice 发送公钥");

        // alice接收到bobPubKeyEnc 后生成 bobPubKey
        // 再进行定位,使得aliceKeyAgree定位在bobPubKey
        KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
        x509EncodedKeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey bobPubKey = aliceKeyFac.generatePublic(x509EncodedKeySpec);
        System.out.println("alice 接收 Bob 公钥并解码成功");

        KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKPair.getPrivate());
        System.out.println("alice: 初始本地化成功");
        aliceKeyAgree.doPhase(bobPubKey, true);
        // alice 生成本地密钥 aliceDESKey
        SecretKey aliceDESKey = aliceKeyAgree.generateSecret("DES");
        System.out.println("alice : 用bob的公钥定位本地可以,并生成DES密钥");

        if (aliceDESKey.equals(bobDESKey)) {
            System.out.println("alice and bob have the same DES key");
        }

        //现在bob和alice的本地DESKey是相同的,所以完全可以进行发送加密,接收后解密,达到安全通道的目的

        // bob用bobDESKey 密码加密信息
        Cipher bobCipher = Cipher.getInstance("DES");
        bobCipher.init(Cipher.ENCRYPT_MODE, bobDESKey);
        String bobInfo = "this is bob's sec info";
        System.out.println("bob's info: " + bobInfo);
        byte[] clearText = bobInfo.getBytes();
        byte[] cipherText = bobCipher.doFinal(clearText);

        // alice use aliceDESKey decrypt
        Cipher aliceCipher = Cipher.getInstance("DES");
        aliceCipher.init(Cipher.DECRYPT_MODE, aliceDESKey);
        byte[] recovered = aliceCipher.doFinal(cipherText);
        System.out.println("alice decrypt bob's info: " + (new String(recovered)));

        if (!Arrays.equals(clearText, recovered)) {
            throw new Exception("the decrypt is different from the original info");
        }

        System.out.println("the decrypt is equal to the original info");




    }
}
