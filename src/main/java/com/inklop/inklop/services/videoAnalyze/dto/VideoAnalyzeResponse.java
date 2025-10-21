package com.inklop.inklop.services.videoAnalyze.dto;

public record VideoAnalyzeResponse(
    Alignment alignment,
    Boolean duplicated
) { 
    public record Alignment(
        Boolean aproved,
        Double match_percentage,
        String reasons
    ) {}  
}
