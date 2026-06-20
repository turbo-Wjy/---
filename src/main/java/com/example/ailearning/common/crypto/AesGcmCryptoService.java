package com.example.ailearning.common.crypto;

import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesGcmCryptoService {
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKeySpec keySpec;

    public AesGcmCryptoService(@Value("${app.crypto.aes-key}") String configuredKey) {
        this.keySpec = new SecretKeySpec(sha256(configuredKey), "AES");
    }

    public EncryptedValue encrypt(String plainText) {
        if (plainText == null) {
            return new EncryptedValue(null, null);
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return new EncryptedValue(Base64.getEncoder().encodeToString(encrypted), Base64.getEncoder().encodeToString(iv));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "敏感信息加密失败");
        }
    }

    public String decrypt(String encryptedText, String ivText) {
        if (encryptedText == null || ivText == null) {
            return null;
        }
        try {
            byte[] encrypted = Base64.getDecoder().decode(encryptedText);
            byte[] iv = Base64.getDecoder().decode(ivText);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "敏感信息解密失败");
        }
    }

    private byte[] sha256(String configuredKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(configuredKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加密密钥初始化失败");
        }
    }

    public record EncryptedValue(String cipherText, String iv) {
    }
}
