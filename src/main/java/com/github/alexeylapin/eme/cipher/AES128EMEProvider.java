package com.github.alexeylapin.eme.cipher;

import java.security.Provider;

public class AES128EMEProvider extends Provider {

    public AES128EMEProvider() {
        super("AES128EME", 1.0, "AES128EME Java Security Provider");
        put("Cipher.AES", AES128EME.class.getName());
        put("Cipher.AES SupportedPaddings", "PKCS7PADDING");
        put("Cipher.AES SupportedModes", "EME");
    }

}
