package com.github.alexeylapin.eme;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public interface EME {

    static EME fromKey(byte[] key) throws Exception {
        return new EMEImpl(key);
    }

    byte[] transform(byte[] tweak, byte[] inputData, Mode mode) throws IllegalBlockSizeException, BadPaddingException;

    default byte[] encrypt(byte[] tweak, byte[] inputData) throws IllegalBlockSizeException, BadPaddingException {
        return transform(tweak, inputData, Mode.ENCRYPT);
    }

    default byte[] decrypt(byte[] tweak, byte[] inputData) throws IllegalBlockSizeException, BadPaddingException {
        return transform(tweak, inputData, Mode.DECRYPT);
    }

    enum Mode {
        ENCRYPT,
        DECRYPT
    }

}
