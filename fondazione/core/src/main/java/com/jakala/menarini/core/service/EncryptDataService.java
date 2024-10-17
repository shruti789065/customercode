package com.jakala.menarini.core.service;

import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;


@Component(
        service = EncryptDataServiceInterface.class,
        immediate = true
)
public class EncryptDataService implements EncryptDataServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptDataService.class);

    private static final String ALGORITHM = "AES";

    private String secretKey;

    @Activate
    private void activate(Map<String, Object> properties) {
        this.secretKey = properties.get("secretKey").toString();
    }


    @Override
    public String encrypt(String dataObject)  {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(this.secretKey), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(dataObject.getBytes());
            String encryptedString = Base64.getEncoder().encodeToString(encryptedBytes);
            LOGGER.info("Encrypted data: {}", encryptedString);
            return encryptedString;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | BadPaddingException  | IllegalBlockSizeException e) {
            LOGGER.error("================== ENC ERROR =================");
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }

    @Override
    public String decrypt(String dataEncrypt) {
        try {
            SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(this.secretKey), ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(dataEncrypt));
            return new String(decryptedBytes);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | BadPaddingException  | IllegalBlockSizeException e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }

    }
}
