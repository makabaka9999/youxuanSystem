package com.youxuan.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

@Service
public class RsaKeyService {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyService.class);
    private static final String ALGORITHM = "RSA";
    private static final String CIPHER_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int KEY_SIZE = 2048;

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public RsaKeyService() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
            log.info("RSA key pair generated successfully, key size: {}", KEY_SIZE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String decrypt(String encryptedBase64) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            // 使用 SHA-256 作为 OAEP 摘要和 MGF1 摘要，与前端 Web Crypto API 的 RSA-OAEP/SHA-256 保持一致
            OAEPParameterSpec oaepSpec = new OAEPParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RSA decrypt failed", e);
            throw new RuntimeException("Password decryption failed", e);
        }
    }
}
