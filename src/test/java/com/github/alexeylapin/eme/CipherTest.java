package com.github.alexeylapin.eme;

import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CipherTest extends EMETestSupport {

    @Test
    void name() throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        String algorithm = cipher.getAlgorithm();
        System.out.println(algorithm);

        int blockSize = cipher.getBlockSize();
        System.out.println(blockSize);

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

//        byte[] iv = cipher.getIV();

        int outputSize = cipher.getOutputSize(18);
        System.out.println(outputSize);

//        cipher.update(new byte[10]);
        cipher.doFinal(new byte[10]);
    }

    @Test
    void name2() throws Exception {
        Security.addProvider(new CustomProvider());
        Cipher cipher = Cipher.getInstance("AES/EME/NoPadding");
//        System.out.println(cipher);

        SecretKeySpec keySpec = new SecretKeySpec(new byte[32], "AES");

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(new byte[16]));

        byte[] update = cipher.update(new byte[512]);

        assertThat(update).isEqualTo(buf9F2E);
    }

    @Test
    void name4() throws Exception {
        Security.addProvider(new CustomProvider());

        byte[] input = new byte[20];

        SecretKeySpec keySpec = new SecretKeySpec(new byte[32], "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);

        Cipher cipher1 = Cipher.getInstance("AES/EME/NoPadding");
        cipher1.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

        byte[] encrypted = cipher1.doFinal(input);

        Cipher cipher2 = Cipher.getInstance("AES/EME/NoPadding");
        cipher2.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decrypted = cipher2.doFinal(encrypted);

        System.out.println(Arrays.toString(encrypted) + " --- " + encrypted.length);
        System.out.println(Arrays.toString(decrypted) + " --- " + decrypted.length);
    }

    @Test
    void name3() throws Exception {
//        new CipherInputStream(new FileInputStream(), )
        PKCS5Padding padding = new PKCS5Padding(16);

        int len = 30;
        int paddedLength;
        if (len % 16 == 0) {
            paddedLength = len;
        } else {
            paddedLength = (len + 16) - (len % 16);
        }

        byte[] bytes = new byte[paddedLength];
        System.out.println(bytes.length);
        padding.padWithLen(bytes, len, paddedLength - len);

        System.out.println(Arrays.toString(bytes));

        int unpad = padding.unpad(bytes, bytes.length - 16, 16);
        System.out.println(unpad);
    }

    @Test
    void name5() {
KeyGenerator
    }

}
