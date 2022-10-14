/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/10/12
 * Time: 10:16
 * Describe:
 */

package com.sifan.basis;


import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;


public class ShiroRun {

    @Test
    public void shiroMd5() {
        // 密码明文
        String password = "123";
        // 使用md5加密
        Md5Hash md5Hash = new Md5Hash(password);
        System.out.println("md5加密 = " + md5Hash.toHex());
        // 带盐加密,盐就是给密码明文拼接一个字符串，然后md5加密
        Md5Hash md5Hash2 = new Md5Hash(password, "sifan");
        System.out.println("md5带盐加密 = " + md5Hash2.toHex());

        // 为了保证数据安全，可以进行多次迭代加密
        Md5Hash md5Hash3 = new Md5Hash(password, "sifan", 3);
        System.out.println("3次md5带盐加密 = " + md5Hash3.toHex());

        // 使用父类SimpleHash进行加密
        SimpleHash simpleHash = new SimpleHash("MD5", password, "sifan", 3);
        System.out.println("3次simple选择md5加密 = " + simpleHash.toHex());

    }

    @Test
    public void shiroSha1() {
        // 用户名
        String username = "sifan";
        // 密码明文
        String password = "123";
        // 为了保证数据安全，可以进行多次迭代加密
        Sha1Hash sha1Hash = new Sha1Hash(password, username, 16);
        System.out.println("16次simple选择sha1加密 = " + sha1Hash.toHex());

        // 使用父类SimpleHash进行加密
        SimpleHash simpleHash = new SimpleHash("Sha1", password, username, 16);
        System.out.println("16次simple选择sha1加密 = " + simpleHash.toHex());
    }
}
