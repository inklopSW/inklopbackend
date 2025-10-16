package com.inklop.inklop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class ScrapperConfig {
    @Value("${external.video.api-url}")
    private String videoApiUrl;

    public String getVideoApiUrl() {
        return videoApiUrl;
    }

}
