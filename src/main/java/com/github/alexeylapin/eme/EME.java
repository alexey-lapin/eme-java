package com.github.alexeylapin.eme;

public interface EME {

    static EME fromKey(byte[] key) throws Exception {
        return new EMEImpl(key);
    }

    byte[] transform(byte[] tweak, byte[] inputData, Mode mode) throws Exception;

    default byte[] encrypt(byte[] tweak, byte[] inputData) throws Exception {
        return transform(tweak, inputData, Mode.ENCRYPT);
    }

    default byte[] decrypt(byte[] tweak, byte[] inputData) throws Exception {
        return transform(tweak, inputData, Mode.DECRYPT);
    }

    enum Mode {
        ENCRYPT,
        DECRYPT
    }

}
