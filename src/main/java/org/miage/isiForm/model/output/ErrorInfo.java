package org.miage.isiForm.model.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ErrorInfo {
    @JsonProperty("type")
    private String type;
    @JsonProperty("message")
    private String message;
    @JsonProperty("stack-trace")
    private List<String> stackTrace = new ArrayList<>();

    private ErrorInfo(Throwable error) {
        this.type       = error.getClass().getName();
        this.message    = new String(error.getMessage().getBytes(), Charset.forName("UTF-8"));
        for(StackTraceElement element : error.getStackTrace()) {
            stackTrace.add(element.toString());
        }
    }

    public static String getJson(Throwable error) {
        try {
            return new ObjectMapper().writeValueAsString(new ErrorInfo(error));
        } catch (Throwable ignored) {
            return "";
        }
    }
}
