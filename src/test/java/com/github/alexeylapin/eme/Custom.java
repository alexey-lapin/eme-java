package com.github.alexeylapin.eme;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class Custom extends CipherSpi {

    private EME eme;
    private byte[] iv;
    private EME.Mode mode;
    private PKCS5Padding padding;

    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        System.out.println("Custom.engineSetMode");
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        System.out.println("Custom.engineSetPadding");
    }

    @Override
    protected int engineGetBlockSize() {
        System.out.println("Custom.engineGetBlockSize");
        return 0;
    }

    @Override
    protected int engineGetOutputSize(int inputLen) {
        System.out.println("Custom.engineGetOutputSize");
        return 0;
    }

    @Override
    protected byte[] engineGetIV() {
        System.out.println("Custom.engineGetIV");
        return new byte[0];
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        System.out.println("Custom.engineGetParameters");
        return null;
    }

    @Override
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        System.out.println("Custom.engineInit");
        throw new UnsupportedOperationException();
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        System.out.println("Custom.engineInit");
        IvParameterSpec ivParameterSpec = (IvParameterSpec) params;
        iv = ivParameterSpec.getIV();
        mode = opmode == 1 ? EME.Mode.ENCRYPT : EME.Mode.DECRYPT;
        padding = new PKCS5Padding(16);

        Cipher encryptor;
        Cipher decryptor;
        try {
            encryptor = Cipher.getInstance("AES/ECB/NoPadding");
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            decryptor = Cipher.getInstance("AES/ECB/NoPadding");
            decryptor.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        eme = new EMEImpl(encryptor, decryptor);
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        System.out.println("Custom.engineInit");
    }

    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        System.out.println("Custom.engineUpdate");
        return transform(input);
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        System.out.println("Custom.engineUpdate");
        return 0;
    }

    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        System.out.println("Custom.engineDoFinal");
        return transform(input);
    }

    private byte[] transform(byte[] input) {
        byte[] transform;
        try {
            if (mode == EME.Mode.ENCRYPT) {
                input = pad(input);
            }
            transform = eme.transform(iv, input, mode);
            if (mode == EME.Mode.DECRYPT) {
                transform = unpad(transform);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return transform;
    }

    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        System.out.println("Custom.engineDoFinal");
        return 0;
    }

    private byte[] pad(byte[] input) {
        int paddedLength;
        int length = input.length;
        if (length % 16 == 0) {
            paddedLength = length;
        } else {
            paddedLength = (length + 16) - (length % 16);
        }

        byte[] padded = new byte[paddedLength];
        System.arraycopy(input, 0, padded, 0, length);
        try {
            padding.padWithLen(padded, length, paddedLength - length);
        } catch (ShortBufferException e) {
            throw new RuntimeException(e);
        }
        return padded;
    }

    private byte[] unpad(byte[] input) {
        int unpad = padding.unpad(input, input.length - 16, 16);
        byte[] unpadded;
        if (unpad > 0) {
            unpadded = new byte[unpad];
            System.arraycopy(input, 0, unpadded, 0, unpad);
        } else {
            unpadded = input;
        }
        return unpadded;
    }

}
