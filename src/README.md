EME for Java [![Maven Central](https://img.shields.io/maven-central/v/com.github.alexey-lapin.eme-cipher/eme-cipher?color=%2349C41B)](https://central.sonatype.com/artifact/com.github.alexey-lapin.eme-cipher/eme-cipher) [![codecov](https://codecov.io/gh/alexey-lapin/eme-java/branch/master/graph/badge.svg?token=N4DYGSK1QZ)](https://codecov.io/gh/alexey-lapin/eme-java)
==========
This is a port of [EME for Go](https://github.com/rfjakob/eme).

**EME** (ECB-Mix-ECB or, clearer, **Encrypt-Mix-Encrypt**) is a wide-block
encryption mode developed by Halevi
and Rogaway in 2003.

EME uses multiple invocations of a block cipher to construct a new
cipher of bigger block size (in multiples of 16 bytes, up to 2048 bytes).

Quoting from the original paper:

> We describe a block-cipher mode of operation, EME, that turns an n-bit block cipher into
> a tweakable enciphering scheme that acts on strings of mn bits, where m ∈ [1..n]. The mode is
> parallelizable, but as serial-efficient as the non-parallelizable mode CMC [6]. EME can be used
> to solve the disk-sector encryption problem. The algorithm entails two layers of ECB encryption
> and a “lightweight mixing” in between. We prove EME secure, in the reduction-based sense of
> modern cryptography.

This is an implementation of EME in Java, complete with test vectors from IEEE and Halevi.

It has no dependencies outside the standard library.

## Usage
Maven:
```xml
<dependency>
    <groupId>com.github.alexey-lapin.eme-cipher</groupId>
    <artifactId>eme-cipher</artifactId>
    <version>@version@</version>
</dependency>
```

Gradle:
```
implementation("com.github.alexey-lapin.eme-cipher:eme-cipher:@version@")
```

### As a javax.crypto.Cipher
```java
import com.github.alexeylapin.eme.cipher.AES128EMEProvider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class Example {

    public static void main(String[] args) throws Exception {
        // register eme provider
        Security.addProvider(new AES128EMEProvider());

        // create cipher
        Cipher cipher = Cipher.getInstance("AES/EME/PKCS7Padding");

        // initialize cipher
        byte[] key = new byte[32];
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        byte[] iv = new byte[16];
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));

        // use cipher
        byte[] data = new byte[100];
        byte[] encrypted = cipher.update(data);
    }
    
}
```

### As a custom impl
```java
import com.github.alexeylapin.eme.EME;

public class Example {

    public static void main(String[] args) throws Exception {
        // initialize cipher
        byte[] key = new byte[32];
        
        EME eme = EME.fromKey(key);
        
        byte[] iv = new byte[16];

        // use cipher
        byte[] data = new byte[512];
        byte[] encrypted = eme.encrypt(iv, data);
    }
    
}
```