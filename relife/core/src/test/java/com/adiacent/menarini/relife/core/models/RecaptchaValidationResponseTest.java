package com.adiacent.menarini.relife.core.models;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecaptchaValidationResponseTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Register the JavaTimeModule to handle LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testGettersAndSetters() {
        RecaptchaValidationResponse response = new RecaptchaValidationResponse();

        // Test success
        response.setSuccess(true);
        assertTrue(response.isSuccess());

        // Test challengeTs
        LocalDateTime now = LocalDateTime.now();
        response.setChallengeTs(now);
        assertEquals(now, response.getChallengeTs());

        // Test errorCodes
        List<String> errorCodes = Arrays.asList("error-code-1", "error-code-2");
        response.setErrorCodes(errorCodes);
        assertEquals(errorCodes, response.getErrorCodes());

        // Test null errorCodes
        response.setErrorCodes(null);
        assertNull(response.getErrorCodes());
    }

    @Test
    public void testErrorCodesImmutable() {
        RecaptchaValidationResponse response = new RecaptchaValidationResponse();

        List<String> errorCodes = Arrays.asList("error-code-1", "error-code-2");
        response.setErrorCodes(errorCodes);

        List<String> retrievedErrorCodes = response.getErrorCodes();
        assertEquals(errorCodes, retrievedErrorCodes);

        // Try to modify the retrieved list and check that it does not affect the original list
        retrievedErrorCodes.set(0, "new-error-code");
        assertNotEquals("new-error-code", response.getErrorCodes().get(0));
    }



}
