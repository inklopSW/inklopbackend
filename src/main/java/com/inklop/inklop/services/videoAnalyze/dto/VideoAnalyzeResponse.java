package com.inklop.inklop.services.videoAnalyze.dto;

public record VideoAnalyzeResponse(
    Boolean duplicated,
    String duplicate_reason,
    String duplicate_candidate_url,
    Alignment alignment
) { 
    public record Alignment(
        Boolean aproved,
        Double match_percent,
        String reasons
    ) {}  
}
