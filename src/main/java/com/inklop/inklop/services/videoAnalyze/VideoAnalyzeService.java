package com.inklop.inklop.services.videoAnalyze;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.inklop.inklop.config.ExtraConfig;
import com.inklop.inklop.services.videoAnalyze.dto.VideoAnalyzeResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VideoAnalyzeService {
    private final ExtraConfig extraConfig;
    private final RestTemplate restTemplate;

    public VideoAnalyzeResponse analyzeVideo(Long campaignId, String videoUrl, LocalDate endDate, String description){
        String apiUrl = extraConfig.getVideoAnalyzeUrl() + "/api/evaluate";

        System.out.println("Description: " + description);

        Map<String, Object> requestBody = Map.of(
            "campaign_id", String.valueOf(campaignId),
            "descripcion", description,
            "video_url", videoUrl,
            "end_date", endDate.toString()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<VideoAnalyzeResponse> response = restTemplate.postForEntity(apiUrl, requestEntity, VideoAnalyzeResponse.class);

        System.out.println("Video Analyze Response Status: " + response.getBody().alignment().match_percent());
        return response.getBody();
        
    }
}
