package com.sun.crypto.provider;

import java.lang.reflect.Field;

public class EmeCipher extends AESCipher {

    protected EmeCipher() {
        super(32);
        try {
            Field core = AESCipher.class.getDeclaredField("core");
            core.setAccessible(true);
            core.set(this, new CipherCore(new AESCrypt(), AESConstants.AES_BLOCK_SIZE));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
