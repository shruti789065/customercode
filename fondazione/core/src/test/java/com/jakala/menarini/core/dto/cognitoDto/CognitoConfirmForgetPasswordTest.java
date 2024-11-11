package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoConfirmForgetPassword;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CognitoConfirmForgetPasswordTest {

    private CognitoConfirmForgetPassword cognitoConfirmForgetPassword;

    @BeforeEach
    void setUp() {
        cognitoConfirmForgetPassword = new CognitoConfirmForgetPassword();
    }

    @Test
    void testGetSetUsername() {
        String username = "testUser";
        cognitoConfirmForgetPassword.setUsername(username);
        assertEquals(username, cognitoConfirmForgetPassword.getUsername());
    }

    @Test
    void testGetSetClientId() {
        String clientId = "testClientId";
        cognitoConfirmForgetPassword.setClientId(clientId);
        assertEquals(clientId, cognitoConfirmForgetPassword.getClientId());
    }

    @Test
    void testGetSetSecretHash() {
        String secretHash = "testSecretHash";
        cognitoConfirmForgetPassword.setSecretHash(secretHash);
        assertEquals(secretHash, cognitoConfirmForgetPassword.getSecretHash());
    }

    @Test
    void testGetSetPassword() {
        String password = "testPassword";
        cognitoConfirmForgetPassword.setPassword(password);
        assertEquals(password, cognitoConfirmForgetPassword.getPassword());
    }

    @Test
    void testGetSetConfirmationCode() {
        String confirmationCode = "testConfirmationCode";
        cognitoConfirmForgetPassword.setConfirmationCode(confirmationCode);
        assertEquals(confirmationCode, cognitoConfirmForgetPassword.getConfirmationCode());
    }
}
