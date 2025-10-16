package com.inklop.inklop.config;
import org.springframework.beans.factory.annotation.Value;

import lombok.ToString;

@ToString
public class ApifyApiConfig {

    @Value("${apify.api.key}")
    private String apifyApiKey;

    public String getApiKey() {
        return apifyApiKey;
    }
}
