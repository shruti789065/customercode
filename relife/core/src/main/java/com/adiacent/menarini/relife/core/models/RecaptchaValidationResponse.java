package com.adiacent.menarini.relife.core.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class RecaptchaValidationResponse {
    private boolean success;

    @JsonProperty("challenge_ts")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime challengeTs;

    @JsonProperty("error-codes")
    private List<String> errorCodes;

    // Costruttori, getter e setter

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getChallengeTs() {
        return challengeTs;
    }

    public void setChallengeTs(LocalDateTime challengeTs) {
        this.challengeTs = challengeTs;
    }

    public List<String> getErrorCodes() {
        if (errorCodes == null) {
            return null;
        }
        String[] array = errorCodes.toArray(new String[errorCodes.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }

    public void setErrorCodes(List<String> errorCodes) {
        if (errorCodes == null) {
            this.errorCodes = null;
        } else {
            String[] array = errorCodes.toArray(new String[errorCodes.size()]);
            String[] clone = array.clone();
            this.errorCodes = Arrays.asList(clone);
        }
    }
}
