package com.adiacent.menarini.menarinimaster.core.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.joda.time.format.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

/*
{
  "success": true|false,
  "challenge_ts": timestamp,  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
  "hostname": string,         // the hostname of the site where the reCAPTCHA was solved
  "error-codes": [...]        // optional
}

{
  "success": true,
  "challenge_ts": "2023-07-19T23:58:18Z",
  "hostname": "localhost"
}

 */
public class RecaptchaValidationResponse {
    private boolean success;
    @JsonProperty("challenge_ts")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZZ")
    private LocalDateTime challengeTs;

    @JsonProperty("error-codes")
    private List<String> errorCodes;

    public RecaptchaValidationResponse() {

    }

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
        return errorCodes;
    }

    public void setErrorCodes(List<String> errorCodes) {
        this.errorCodes = errorCodes;
    }
}
