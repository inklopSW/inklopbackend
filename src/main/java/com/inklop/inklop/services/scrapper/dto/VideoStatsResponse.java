package com.inklop.inklop.services.scrapper.dto;

public record VideoStatsResponse(
    String platform,
    String timestamp,
    String caption,
    Long views,
    Long likes,
    Long comments,
    Long shares,
    Long bookmarks,
    String video_url,
    String display_image,
    String channel_url,
    String channel_image
) {
    
}
