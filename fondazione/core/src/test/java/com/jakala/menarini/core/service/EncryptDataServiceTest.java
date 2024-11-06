package com.jakala.menarini.core.service;

import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.osgi.service.component.annotations.Activate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EncryptDataServiceTest {

    private EncryptDataService encryptDataService;

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        encryptDataService = new EncryptDataService();
    }

    @Test
    void testEncrypt() {
        setPrivateField(encryptDataService, "secretKey", "MinimumAESKeyLength24B");
        String data = "testData";
        String encryptedData = encryptDataService.encrypt(data);
        assertNotNull(encryptedData);
        assertFalse(encryptedData.isEmpty());
    }

    @Test
    void testDecrypt() {
        setPrivateField(encryptDataService, "secretKey", "MinimumAESKeyLength24B");
        String data = "testData";
        String encryptedData = encryptDataService.encrypt(data);
        String decryptedData = encryptDataService.decrypt(encryptedData);
        assertEquals(data, decryptedData);
    }

    @Test
    void testEncryptWithException() {
        setPrivateField(encryptDataService, "secretKey", "invalidKey");
        String encryptedData = encryptDataService.encrypt("testData");
        assertEquals("", encryptedData);
    }

    @Test
    void testDecryptWithException() {
        setPrivateField(encryptDataService, "secretKey", "invalidKey");
        String decryptedData = encryptDataService.decrypt("invalidData");
        assertEquals("", decryptedData);
    }
}