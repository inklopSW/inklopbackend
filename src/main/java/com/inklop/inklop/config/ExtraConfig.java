package com.inklop.inklop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class ExtraConfig {
    @Value("${external.video.analyze-url}")
    private String videoAnalyzeUrl;

    public String getVideoAnalyzeUrl() {
        return videoAnalyzeUrl;
    }
    
}
