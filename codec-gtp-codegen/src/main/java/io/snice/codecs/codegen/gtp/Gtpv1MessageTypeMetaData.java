package io.snice.codecs.codegen.gtp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

public class Gtpv1MessageTypeMetaData {

    private int type;
    private String message;

    @JsonProperty("gtpc")
    private boolean isGtpC;

    @JsonProperty("gtpu")
    private boolean isGtpU;

    @JsonProperty("gtp_prime")
    private boolean isGtpPrime;

    private String reference;

    public String getMessage() {
        return message;
    }

    public boolean isGtpC() {
        return isGtpC;
    }

    public boolean isGtpU() {
        return isGtpU;
    }

    public boolean isGtpPrime() {
        return isGtpPrime;
    }

    public String getReference() {
        return reference;
    }

    public int getType() {
        return type;
    }

}
