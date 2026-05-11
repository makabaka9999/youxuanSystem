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

/**
 * RSA 密钥服务。
 * <p>
 * 在应用启动时生成 RSA-2048 密钥对，提供公钥获取和密码解密功能。
 * 支持 OAEPWithSHA-256 填充方案，与前端的 Web Crypto API（RSA-OAEP/SHA-256）兼容。
 * </p>
 */
@Service
public class RsaKeyService {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyService.class);

    /** RSA 算法名称 */
    private static final String ALGORITHM = "RSA";

    /** 加密算法/模式/填充（与前端 Web Crypto API 兼容） */
    private static final String CIPHER_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /** 密钥长度：2048 位 */
    private static final int KEY_SIZE = 2048;

    /** RSA 公钥，用于前端加密密码 */
    private final PublicKey publicKey;

    /** RSA 私钥，用于后端解密密码 */
    private final PrivateKey privateKey;

    /**
     * 构造 RSA 密钥服务。
     * <p>
     * 实例化时自动生成 RSA-2048 密钥对。
     * </p>
     */
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

    /**
     * 获取 Base64 编码的公钥字符串。
     *
     * @return Base64 编码的公钥
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 解密前端使用 RSA 公钥加密的密码。
     * <p>
     * 使用 OAEPPadding 和 SHA-256 作为 OAEP 摘要及 MGF1 摘要，
     * 与前端的 Web Crypto API（RSA-OAEP/SHA-256）保持一致。
     * </p>
     *
     * @param encryptedBase64 前端加密后的 Base64 字符串
     * @return 解密后的明文字符串（UTF-8 编码）
     * @throws RuntimeException 解密失败时抛出
     */
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
