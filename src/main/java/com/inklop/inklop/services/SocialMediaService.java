package com.inklop.inklop.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.inklop.inklop.entities.User;
import com.inklop.inklop.controllers.user.request.SocialMediaRequest;
import com.inklop.inklop.controllers.user.response.SocialMediaResponse;
import com.inklop.inklop.entities.SocialMedia;
import com.inklop.inklop.entities.valueObject.user.Platform;
import com.inklop.inklop.entities.valueObject.user.UserRole;
import com.inklop.inklop.mappers.SocialMediaMapper;
import com.inklop.inklop.repositories.SocialMediaRepository;
import com.inklop.inklop.services.scrapper.ScrapperService;
import com.inklop.inklop.services.scrapper.dto.ProfileResponse;

import jakarta.transaction.Transactional;
import lombok.*;

@Service
@RequiredArgsConstructor
public class SocialMediaService {
    private static final Logger log = LoggerFactory.getLogger(SocialMediaService.class);

    private final SocialMediaMapper socialMediaMapper;
    private final SocialMediaRepository socialMediaRepository;
    private final ScrapperService scrapperService;


    /**
     * Construye la entidad a partir del request y user.
     * Si requiere scrapping y este falla o devuelve null, retorna null para indicar que se debe omitir.
     */
    public SocialMedia getSocialMediaEntity(SocialMediaRequest socialMedia, User user){
        String url = "";
        String ownerId = "";
        String name = "";
        String avatar = "";
        String nickname = "";

        Platform platform = socialMedia.platform();

        boolean isCreator = user != null && UserRole.CREATOR.equals(user.getUserRole());

        // Solo intentar scrap si es Instagram/TikTok y el user es Creator
        if ((Platform.INSTAGRAM.equals(platform) || Platform.TIKTOK.equals(platform)) && isCreator) {
            try {
                ProfileResponse profile = scrapperService.getProfile(socialMedia.link(), platform);
                if (profile == null) {
                    log.warn("getSocialMediaEntity -> Scrapper returned null for url={} platform={} userId={}", socialMedia.link(), platform, user != null ? user.getId() : null);
                    return null; // omitir si no hay datos
                }
                url = profile.link() != null ? profile.link() : socialMedia.link();
                ownerId = profile.ownerId() != null ? profile.ownerId() : "";
                name = profile.name() != null ? profile.name() : "";
                avatar = profile.channel_image() != null ? profile.channel_image() : "";
                nickname = profile.nickname() != null ? profile.nickname() : "";
            } catch (Exception e) {
                // No lanzar: solo omitir este social media y seguir con los demás
                log.warn("getSocialMediaEntity -> Failed to scrap url={} platform={} userId={} error={}", socialMedia.link(), platform, user != null ? user.getId() : null, e.getMessage());
                return null;
            }
        } else {
            // No scrap requerido: usar el link proporcionado
            url = socialMedia.link();
        }

        return socialMediaMapper.toEntity(platform, url, ownerId, name, avatar, nickname, user);
    }

    public SocialMediaResponse getSocialMediaResponse(SocialMedia socialMedia){
        return socialMediaMapper.toResponse(socialMedia);
    }

    /**
     * Procesa y guarda los social media. Si el scrapping de una entrada falla o devuelve null,
     * esa entrada se omite y no provoca rollback del resto.
     */
    @Transactional
    public List<SocialMediaResponse> addSocialMedias(List<SocialMediaRequest> socialMedias, User user){
        if (socialMedias == null || socialMedias.isEmpty()) {
            return List.of();
        }

        List<SocialMedia> toSave = new ArrayList<>();

        for (SocialMediaRequest req : socialMedias) {
            SocialMedia entity = getSocialMediaEntity(req, user);
            if (entity == null) {
                log.info("addSocialMedias -> Skipping social media for link={} platform={} userId={}", req.link(), req.platform(), user != null ? user.getId() : null);
                continue;
            }
            // Ensure new row and set owner
            entity.setId(null);
            entity.setUser(user);
            toSave.add(entity);
        }

        if (toSave.isEmpty()) {
            log.info("addSocialMedias -> No valid social media to save for userId={}", user != null ? user.getId() : null);
            return List.of();
        }

        List<SocialMedia> saved = socialMediaRepository.saveAll(toSave);

        return saved.stream()
                .map(socialMediaMapper::toResponse)
                .toList();
    }
}
