package org.miage.isiForm.model.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;

public class Message {
    @JsonProperty("message")
    private String message;

    private Message(String message) {
        this.message = new String(message.getBytes(), Charset.forName("UTF-8"));
    }

    public static String getJson(String message) {
        try {
            return new ObjectMapper().writeValueAsString(new Message(message));
        } catch (Throwable error) {
            return ErrorInfo.getJson(error);
        }
    }
}
