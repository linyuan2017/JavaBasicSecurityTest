package com.example.digitalsignature;

import android.content.Context;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class TestDSA {

    Context mContext;

    public TestDSA(Context context) {
        mContext = context;
    }

    public void test() {

        // 数字签名生成密钥

        // 第一步生成密钥对,如果已经生成过,本过程就可以跳过
        // 对用户来讲 myprikey.dat 要保存在本地
        // 而mypubkey.dat 给发布给其他用户
        if ((new File("myprikey.dat")).exists() == false) {
            if (generatekey() == false) {
                System.out.println("生成密钥对失败");
                return;
            }
        }

        // 第二步, 此用户
        // 从文件中读入私钥, 对一个字符串进行签名后保存字一个文件(myinfo.dat)中
        // 并且把myinfo.dat发送出去
        // 为了方便数字签名也放进了 myinfo.dat文件中, 当然也可以分别发送
        try {
            File file = new File(mContext.getFilesDir(), "myprikey.dat");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            PrivateKey myprikey = (PrivateKey) in.readObject();
            in.close();

            String myinfo = "这是我的信息"; // 要签名的信息
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance("DSA");
            signature.initSign(myprikey);
            signature.update(myinfo.getBytes());
            byte[] signed = signature.sign(); // 对信息的数字签名
            System.out.println("signed(签名内容)=" + byte2hex(signed));

            // 把信息和数字签名保存在一个文件中
            File file1 = new File(mContext.getFilesDir(), "myinfo.dat");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file1));
            out.writeObject(myinfo);
            out.writeObject(signed);
            out.close();
            System.out.println("签名并生成文件成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("签名并生成文件失败");
        }

        // 第三步
        // 其他人通过公共方式得到此用户的公钥和文件
        // 其他人用此用户的公钥, 对文件进行检查, 如果成功说明是此用户发布的信息

        try {
            File file2 = new File(mContext.getFilesDir(), "mypubkey.dat");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file2));
            PublicKey publicKey = (PublicKey) in.readObject();
            in.close();
            System.out.println(publicKey.getFormat());
            File file3 = new File(mContext.getFilesDir(), "myinfo.dat");
            in = new ObjectInputStream(new FileInputStream(file3));
            String info = (String) in.readObject();
            byte[] signed = (byte[]) in.readObject();
            in.close();
            Signature signature = Signature.getInstance("DSA");
            signature.initVerify(publicKey);
            signature.update(info.getBytes());
            if (signature.verify(signed)) {
                System.out.println("info=" + info);
                System.out.println("签名正常");
            } else {
                System.out.println("非正常签名");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 生成一对文件 myprikey.dat 和 mypubkey.dat --- 私钥和公钥
     * 公钥要用户发送给其他用户, 私钥保存在本地
     * @return
     */
    public boolean generatekey() {

        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("DSA");
            keygen.initialize(1024);
            KeyPair keys = keygen.generateKeyPair();
            PublicKey publicKey = keys.getPublic();
            PrivateKey privateKey = keys.getPrivate();
            File file = new File(mContext.getFilesDir(), "myprikey.dat");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(privateKey);
            out.close();
            System.out.println("写入对象 prikeys ok");
            File file1 = new File(mContext.getFilesDir(), "mypubkey.dat");
            out = new ObjectOutputStream(new FileOutputStream(file1));
            out.writeObject(publicKey);
            out.close();
            System.out.println("写入对象 pubkeys ok");
            System.out.println("生成密钥对成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成密钥对失败");
            return false;
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
