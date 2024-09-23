package com.github.alexeylapin.eme.cipher;

import com.github.alexeylapin.eme.EMETestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class CipherTest extends EMETestSupport {

    public static final String AES_EME_PKCS_7_PADDING = "AES/EME/PKCS7Padding";

    @Test
    void should_returnCipherPropertiesCorrectly() throws Exception {
        Security.addProvider(new AES128EMEProvider());

        Cipher cipher = Cipher.getInstance(AES_EME_PKCS_7_PADDING);

        SecretKeySpec keySpec = new SecretKeySpec(new byte[32], "AES");
        byte[] iv = new byte[16];
        ThreadLocalRandom.current().nextBytes(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));

        assertThat(cipher.getAlgorithm()).isEqualTo(AES_EME_PKCS_7_PADDING);
        assertThat(cipher.getBlockSize()).isEqualTo(16);
        assertThat(cipher.getIV()).isEqualTo(iv);
        assertThat(cipher.getParameters()).isNull();
        assertThat(cipher.getOutputSize(0)).isEqualTo(16);
        assertThat(cipher.getOutputSize(1)).isEqualTo(16);
        assertThat(cipher.getOutputSize(5)).isEqualTo(16);
        assertThat(cipher.getOutputSize(16)).isEqualTo(32);
        assertThat(cipher.getOutputSize(17)).isEqualTo(32);
    }

    @ValueSource(ints = {0, 1, 5, 16, 17, 32, 33})
    @ParameterizedTest
    void should_encryptDecryptCorrectly(int length) throws Exception {
        Security.addProvider(new AES128EMEProvider());

        byte[] input = new byte[length];
        for (int i = 0; i < input.length; i++) {
            input[i] = (byte) i;
        }

        SecretKeySpec keySpec = new SecretKeySpec(new byte[32], "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);

        Cipher encryptCipher = Cipher.getInstance(AES_EME_PKCS_7_PADDING);
        encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

        byte[] encrypted = encryptCipher.doFinal(input);

        Cipher decryptCipher = Cipher.getInstance(AES_EME_PKCS_7_PADDING);
        decryptCipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decrypted = decryptCipher.doFinal(encrypted);

        assertThat(decrypted).isEqualTo(input);
    }

    @Test
    void should_updateCorrectly() throws Exception {
        Security.addProvider(new AES128EMEProvider());

        byte[] input = new byte[20];
        for (int i = 0; i < input.length; i++) {
            input[i] = (byte) i;
        }

        SecretKeySpec keySpec = new SecretKeySpec(new byte[32], "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);

        Cipher encryptCipher = Cipher.getInstance(AES_EME_PKCS_7_PADDING);
        encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

        Cipher decryptCipher = Cipher.getInstance(AES_EME_PKCS_7_PADDING);
        decryptCipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] encryptedPart1 = encryptCipher.update(input);
        byte[] encryptedPart2 = encryptCipher.update(input, 0, 10);
        byte[] encryptedOutput3 = new byte[17];
        int encryptedLength3 = encryptCipher.update(input, 1, 12, encryptedOutput3);
        byte[] encryptedOutput4 = new byte[17];
        int encryptedLength4 = encryptCipher.update(input, 2, 15, encryptedOutput4, 1);
        byte[] encryptedFinal = encryptCipher.doFinal();

        byte[] decryptedPart1 = decryptCipher.update(encryptedPart1);
        byte[] decryptedPart2 = decryptCipher.update(encryptedPart2);
        byte[] decryptedPart3 = decryptCipher.update(encryptedOutput3, 0, 16);
        byte[] decryptedPart4 = decryptCipher.update(encryptedOutput4, 1, encryptedLength4);
        byte[] decryptedFinal = decryptCipher.doFinal(encryptedFinal);

        assertThat(decryptedPart1).isEqualTo(input);
        assertThat(decryptedPart2).isEqualTo(Arrays.copyOfRange(input, 0, 0 + 10));
        assertThat(encryptedLength3).isEqualTo(16);
        assertThat(decryptedPart3).isEqualTo(Arrays.copyOfRange(input, 1, 1 + 12));
        assertThat(encryptedLength4).isEqualTo(16);
        assertThat(decryptedPart4).isEqualTo(Arrays.copyOfRange(input, 2, 2 + 15));
        assertThat(decryptedFinal).isEmpty();
    }

}
