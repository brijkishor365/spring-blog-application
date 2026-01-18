package com.qburst.blog_application.security;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public final class KeyLoader {

    private KeyLoader() {
    }

    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        String key = readKey(filename)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public static PublicKey loadPublicKey(String filename) throws Exception {
        String key = readKey(filename)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
    }

    private static String readKey(String filename) throws Exception {
        try (InputStream is = KeyLoader.class.getClassLoader().getResourceAsStream("keys/" + filename)) {

            if (is == null) {
                throw new IllegalStateException("Key not found: " + filename);
            }
            return new String(is.readAllBytes());
        }
    }
}
