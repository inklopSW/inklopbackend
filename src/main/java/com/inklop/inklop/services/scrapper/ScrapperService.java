package com.inklop.inklop.services.scrapper;

import com.inklop.inklop.config.ScrapperConfig;
import com.inklop.inklop.services.scrapper.dto.PostResponse;
import com.inklop.inklop.services.scrapper.dto.ProfileResponse;
import com.inklop.inklop.services.scrapper.dto.ValueVideo;
import com.inklop.inklop.services.scrapper.dto.VideoStatsResponse;

import lombok.AllArgsConstructor;

import com.inklop.inklop.entities.valueObject.user.Platform; // usa tu propio enum
import com.inklop.inklop.mappers.ScrapperMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class ScrapperService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScrapperService.class);

    @Autowired
    private ScrapperConfig scrapperConfig;

    private final ScrapperMapper scrapperMapper;

    @Autowired
    private RestTemplate restTemplate; // se inyecta desde RestTemplateConfig

    public ValueVideo postVideoToExternalApi(String videoUrl, Platform platform, LocalDate finalDate) {
        // validation
        if (!platform.equals(Platform.INSTAGRAM) && !platform.equals(Platform.TIKTOK)) {
            throw new IllegalArgumentException("Platform not supported for video posting");
        }
        
        String apiUrl = scrapperConfig.getVideoApiUrl() + "/videos";

        // Crear el cuerpo del request
        Map<String, Object> requestBody = Map.of(
            "platform", platform.toString(),    
            "url", videoUrl,
            "final_date", finalDate.toString()
        );

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear la entidad HTTP
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Ejecutar la petición POST
        ResponseEntity<PostResponse> response =
            restTemplate.postForEntity(apiUrl, requestEntity, PostResponse.class);

        // Retornar la respuesta
        PostResponse postResponse = response.getBody();
        
        return new ValueVideo(postResponse.video_url(), postResponse.owner_id());
    }

    public Map<String, VideoStatsResponse> getAllPosts(List<String> videoUrls){
        //solucionar pq ta cagado
        List<String> uniqueUrls = videoUrls.stream()
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        if (uniqueUrls.isEmpty()) {
            return Map.of(); // Evita llamadas innecesarias si no hay URLs válidas
        }

        String apiUrl = scrapperConfig.getVideoApiUrl() + "/scrap/all"; // endpoint base

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, List<String>> body = Map.of("urls", uniqueUrls);
            HttpEntity<Map<String, List<String>>> requestEntity = new HttpEntity<>(body, headers);
            log.info("getAllPosts -> Request body: {}", requestEntity);
            ResponseEntity<PostResponse[]> response = restTemplate.postForEntity(
                apiUrl,
                requestEntity,
                PostResponse[].class
                );
            
            PostResponse[] postsArray = response.getBody();

            if (postsArray == null || postsArray.length == 0) {
                log.warn("getAllPosts -> Empty response body from scrapper service");
                return Map.of();
            }

            Map<String, VideoStatsResponse> videoStatsByUrl = Arrays.stream(postsArray)
                .filter(post -> post.video_url() != null)
                .collect(Collectors.toMap(
                    PostResponse::video_url, // key
                    scrapperMapper::toVideoStatsResponse,
                    (existing, duplicate) -> existing // manejar duplicados si es necesario
                ));

            return videoStatsByUrl;


        } catch (HttpClientErrorException e){
            log.error("getAllPosts -> Error calling scrapper service: {}", e.getMessage());
            throw e; // o manejar el error según sea necesario
        } catch (Exception e) {
            log.error("getAllPosts -> Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Unexpected error calling scrapper service", e);
        }

    }

    public ProfileResponse getProfile(String videoUrl, Platform platform){
        if (!platform.equals(Platform.INSTAGRAM) && !platform.equals(Platform.TIKTOK)) {
            throw new IllegalArgumentException("Platform not supported for profile fetching");
        }

        String apiUrl = scrapperConfig.getVideoApiUrl() + "/influencers";

        Map<String, Object> requestBody = Map.of(
            "platform", platform.toString(),
            "link", videoUrl
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ProfileResponse> response = restTemplate.postForEntity(
            apiUrl,
            requestEntity,
            ProfileResponse.class);

        return response.getBody();
    }


    public VideoStatsResponse updateScrap(String url){
        String apiUrl = scrapperConfig.getVideoApiUrl() + "/scrap/refresh?url=" + url;

        log.info(apiUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PostResponse> response = restTemplate.exchange(
            apiUrl,
            HttpMethod.GET,
            requestEntity,
            PostResponse.class
            );

        if (response.getBody() == null) {
            throw new RuntimeException("No data returned from scrapper service for URL: " + url);
        }

        return scrapperMapper.toVideoStatsResponse(response.getBody());    
    }

    


}
