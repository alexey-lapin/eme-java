package com.github.alexeylapin.eme.cipher;

import com.github.alexeylapin.eme.EME;
import com.github.alexeylapin.eme.EMEImpl;

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
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class AES128EME extends CipherSpi {

    private EME eme;
    private EME.Mode mode;
    private byte[] iv;
    private PKCS7Padding padding;

    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        if (!"EME".equalsIgnoreCase(mode)) {
            throw new NoSuchAlgorithmException("mode " + mode + " not supported");
        }
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        if (!"PKCS7Padding".equalsIgnoreCase(padding)) {
            throw new NoSuchPaddingException("padding " + padding + " not supported");
        }
    }

    @Override
    protected int engineGetBlockSize() {
        return 16;
    }

    @Override
    protected int engineGetOutputSize(int inputLen) {
        if (mode == EME.Mode.ENCRYPT) {
            int remainder = inputLen % engineGetBlockSize();
            return inputLen + engineGetBlockSize() - remainder;
        } else if (mode == EME.Mode.DECRYPT) {
            return inputLen;
        } else {
            throw new IllegalStateException("Cipher not initialized");
        }
    }

    @Override
    protected byte[] engineGetIV() {
        if (iv == null) {
            return null;
        }
        return Arrays.copyOf(iv, iv.length);
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override
    protected void engineInit(int opmode, Key key, SecureRandom random) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random)
            throws InvalidKeyException, InvalidAlgorithmParameterException {
        IvParameterSpec ivParameterSpec = (IvParameterSpec) params;
        iv = ivParameterSpec.getIV();
        mode = opmode == 1 ? EME.Mode.ENCRYPT : EME.Mode.DECRYPT;
        padding = new PKCS7Padding(engineGetBlockSize());

        Cipher encryptor;
        try {
            encryptor = Cipher.getInstance(EMEImpl.AES_ECB_NO_PADDING);
            encryptor.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new InvalidAlgorithmParameterException("failed to initialize encryptor", e);
        }

        Cipher decryptor;
        try {
            decryptor = Cipher.getInstance(EMEImpl.AES_ECB_NO_PADDING);
            decryptor.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new InvalidAlgorithmParameterException("failed to initialize decryptor", e);
        }

        eme = new EMEImpl(encryptor, decryptor);
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        try {
            return transform(input, inputOffset, inputLen);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new ProviderException(e);
        }
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
            throws ShortBufferException {
        byte[] transformedInput = engineUpdate(input, inputOffset, inputLen);
        if (outputOffset + transformedInput.length > output.length) {
            throw new ShortBufferException("Output buffer too short for transformation result");
        }
        System.arraycopy(transformedInput, 0, output, outputOffset, transformedInput.length);
        return transformedInput.length;
    }

    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen)
            throws IllegalBlockSizeException, BadPaddingException {
        if (input == null) {
            input = new byte[0];
            inputOffset = 0;
            inputLen = 0;
        }
        return transform(input, inputOffset, inputLen);
    }

    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
            throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] transformedInput = engineDoFinal(input, inputOffset, inputLen);
        if (outputOffset + transformedInput.length > output.length) {
            throw new ShortBufferException("Output buffer too short for transformation result");
        }
        System.arraycopy(transformedInput, 0, output, outputOffset, transformedInput.length);
        return transformedInput.length;
    }

    private byte[] transform(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        byte[] transform;
        byte[] data = Arrays.copyOfRange(input, inputOffset, inputOffset + inputLen);
        if (mode == EME.Mode.ENCRYPT) {
            data = pad(data);
        }
        transform = eme.transform(iv, data, mode);
        if (mode == EME.Mode.DECRYPT) {
            transform = unpad(transform);
        }
        return transform;
    }

    private byte[] pad(byte[] input) {
        int blockSize = engineGetBlockSize();
        int length = input.length;
        int remainder = length % blockSize;
        int paddingLength = blockSize - remainder;
        int paddedLength = length + paddingLength;

        byte[] padded = Arrays.copyOf(input, paddedLength);
        if (paddingLength > 0) {
            try {
                padding.padWithLen(padded, length, paddingLength);
            } catch (ShortBufferException e) {
                // should never happen
                throw new ProviderException("Unexpected exception", e);
            }
        }
        return padded;
    }

    private byte[] unpad(byte[] input) {
        int unpadIndex = padding.unpad(input, input.length - 16, 16);
        byte[] unpadded;
        if (unpadIndex >= 0) {
            unpadded = new byte[unpadIndex];
            System.arraycopy(input, 0, unpadded, 0, unpadIndex);
        } else {
            unpadded = input;
        }
        return unpadded;
    }

}
