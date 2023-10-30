package com.adiacent.menarini.menarinimaster.core.models;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class RecaptchaValidationResponseTest {

//    @Test
    void testGetMessage() throws Exception {
        // some very basic junit tests
        RecaptchaValidationResponse obj = new RecaptchaValidationResponse();
        obj.setSuccess(true);
        LocalDateTime now = LocalDateTime.now();
        obj.setChallengeTs(now);
        obj.setErrorCodes(null);


        assertNull(obj.getErrorCodes());
        assertTrue(obj.isSuccess());
        assertTrue(obj.getChallengeTs().isBefore(LocalDateTime.now()));
    }
}
