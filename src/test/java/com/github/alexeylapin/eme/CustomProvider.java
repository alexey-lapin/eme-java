package com.github.alexeylapin.eme;

import com.sun.crypto.provider.EmeCipher;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

public class CustomProvider extends Provider {

    public static final String NAME = "Custom";

    public CustomProvider() {
        super(NAME, 1.0, "Custom Java Security Provider");
        AccessController.doPrivileged((PrivilegedAction<?>) () -> {

            // Install Caesar cipher
            put("Cipher.AES EME", Custom.class.getName());
            put("Cipher.AES/EME/NoPadding", Custom.class.getName());
//            put("Cipher.AES/EME/NoPadding", EmeCipher.class.getName());
//                put("Cipher.AES_128/EME/NoPadding", Custom.class.getName());
//                put("Cipher.AES_128/EME/PKCS5Padding", Custom.class.getName());
            put("Cipher.AES SupportedPaddings", "NOPADDING|PKCS5PADDING|ISO10126PADDING");
            put("Cipher.AES SupportedModes", "EME");
            return null;
        });
    }
}
