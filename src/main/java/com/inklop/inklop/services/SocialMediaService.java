package com.inklop.inklop.services;

import java.util.List;

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
    private final SocialMediaMapper socialMediaMapper;
    private final SocialMediaRepository socialMediaRepository;
    private final ScrapperService scrapperService;


    @Transactional
    public SocialMedia getSocialMediaEntity(SocialMediaRequest socialMedia, User user){
        String url ="", ownerId ="", name ="", avatar ="", nickname ="";
        //scrapping de datos
        //logic w another social media
        if (socialMedia.platform().equals(Platform.INSTAGRAM) || socialMedia.platform().equals(Platform.TIKTOK)){
            //call to instagram or tiktok api
            //get data
            if (user.getUserRole().equals(UserRole.CREATOR)){
                ProfileResponse profile = scrapperService.getProfile(socialMedia.link(), socialMedia.platform());
                url= profile.link();
                ownerId= profile.ownerId();
                name= profile.name();
                avatar= profile.channel_image();
                nickname= profile.nickname();
            }
        
            
        } else {
            url= socialMedia.link();
        }

        return socialMediaMapper.toEntity(socialMedia.platform(), url, ownerId, name, avatar, nickname, user);
    }

    public SocialMediaResponse getSocialMediaResponse(SocialMedia socialMedia){
        return socialMediaMapper.toResponse(socialMedia);
    }   


    @Transactional
    public List<SocialMediaResponse> addSocialMedias(List<SocialMediaRequest> socialMedias, User user){
        // Si el user tiene asociada una entidad Creator (opcional), úsala como owner

        List<SocialMedia> socialMediaEntities = socialMedias.stream()
                .map(req -> getSocialMediaEntity(req, user))
                .map(entity -> {
                    // Asegura que se creen filas nuevas (no merge de existentes)
                    entity.setId(null);
                    entity.setUser(user);
                    return entity;
                })
                .toList();

        socialMediaRepository.saveAll(socialMediaEntities);

        return socialMediaEntities.stream()
                .map(socialMediaMapper::toResponse)
                .toList();
    }
}
