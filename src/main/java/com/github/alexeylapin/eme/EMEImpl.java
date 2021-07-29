package com.github.alexeylapin.eme;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class EMEImpl implements EME {

    private static final String AES = "AES";
    private static final String AES_ECB_NO_PADDING = "AES/ECB/NoPadding";

    private final Cipher encryptor;
    private final Cipher decryptor;

    public EMEImpl(Cipher encryptor, Cipher decryptor) {
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }

    public EMEImpl(byte[] key) throws Exception {
        SecretKeySpec skSpec = new SecretKeySpec(key, AES);
        this.encryptor = Cipher.getInstance(AES_ECB_NO_PADDING);
        encryptor.init(Cipher.ENCRYPT_MODE, skSpec);
        this.decryptor = Cipher.getInstance(AES_ECB_NO_PADDING);
        decryptor.init(Cipher.DECRYPT_MODE, skSpec);
    }

    public byte[] transform(byte[] tweak, byte[] inputData, Mode mode) throws Exception {
        byte[] T = tweak;
        byte[] P = inputData;
        if (T.length != 16) {
            String message = String.format("tweak must be 16 bytes long, is %d", T.length);
            throw new IllegalArgumentException(message);
        }
        if (P.length % 16 != 0) {
            String message = String.format("data must be a multiple of 16 long, is %d", P.length);
            throw new IllegalArgumentException(message);
        }
        int m = P.length / 16;
        if (m == 0 || m > 16 * 8) {
            String message = String.format("data must be from 1 to %d blocks long, is %d", 16 * 8, m);
            throw new IllegalArgumentException(message);
        }

        byte[] C = new byte[P.length];

        byte[][] LTable = tabulateL(m);

        for (int j = 0; j < m; j++) {
            byte[] Pj = Arrays.copyOfRange(inputData, j * 16, (j + 1) * 16);
            byte[] PPj = xorBlocks(Pj, LTable[j]);
            byte[] out = aesTransform(PPj, mode);
            System.arraycopy(out, 0, C, j * 16, out.length);
        }

        byte[] CView = new byte[16];
        System.arraycopy(C, 0, CView, 0, 16);
        byte[] MP = xorBlocks(CView, T);
        for (int j = 1; j < m; j++) {
            System.arraycopy(C, j * 16, CView, 0, 16);
            MP = xorBlocks(MP, CView);
        }

        byte[] MC = aesTransform(MP, mode);
        byte[] M = xorBlocks(MP, MC);

        byte[] CCCj;
        for (int j = 1; j < m; j++) {
            M = multByTwo(M);
            System.arraycopy(C, j * 16, CView, 0, 16);
            CCCj = xorBlocks(CView, M);
            System.arraycopy(CCCj, 0, C, j * 16, 16);
        }

        byte[] CCC1;
        CCC1 = xorBlocks(MC, T);
        for (int j = 1; j < m; j++) {
            System.arraycopy(C, j * 16, CView, 0, 16);
            CCC1 = xorBlocks(CCC1, CView);
        }
        System.arraycopy(CCC1, 0, C, 0, 16);

        for (int j = 0; j < m; j++) {
            System.arraycopy(C, j * 16, CView, 0, 16);
            System.arraycopy(aesTransform(CView, mode), 0, C, j * 16, 16);
            System.arraycopy(C, j * 16, CView, 0, 16);
            System.arraycopy(xorBlocks(CView, LTable[j]), 0, C, j * 16, 16);
        }

        return C;
    }

    private byte[] aesTransform(byte[] src, Mode mode) throws Exception {
        Cipher cipher = Mode.ENCRYPT == mode ? encryptor : decryptor;
        return cipher.doFinal(src);
    }

    private byte[][] tabulateL(int m) throws Exception {
        byte[] eZero = new byte[16];
        byte[] Li = encryptor.doFinal(eZero);
        byte[][] LTable = new byte[m][];
        for (int i = 0; i < m; i++) {
            Li = multByTwo(Li);
            LTable[i] = Li;
        }
        return LTable;
    }

    private static byte[] xorBlocks(byte[] in1, byte[] in2) {
        if (in1.length != in2.length) {
            String message = String.format("in1.length=%d is not equal to in2.length=%d", in1.length, in2.length);
            throw new IllegalArgumentException(message);
        }
        byte[] result = new byte[in1.length];
        for (int i = 0; i < in1.length; i++) {
            result[i] = (byte) (in1[i] ^ in2[i]);
        }
        return result;
    }

    private static byte[] multByTwo(byte[] in) {
        if (in.length != 16) {
            String message = "in.length=%d must be 16";
            throw new IllegalArgumentException(message);
        }
        byte[] result = new byte[16];
        result[0] = (byte) (in[0] * 2);
        if (in[15] < 0) {
            result[0] ^= 135;
        }
        for (int j = 1; j < 16; j++) {
            result[j] = (byte) (in[j] * 2);
            if (in[j - 1] < 0) {
                result[j] += 1;
            }
        }
        return result;
    }

}
