package com.inklop.inklop.mappers;

import com.inklop.inklop.entities.User;
import com.inklop.inklop.controllers.user.response.SocialMediaResponse;
import com.inklop.inklop.entities.SocialMedia;
import com.inklop.inklop.entities.valueObject.user.Platform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SocialMediaMapper {

    SocialMedia toEntity(
        Platform platform, 
        String link, 
        String ownerId, 
        String name, 
        String avatar, 
        String nickname,
        User user
        );

    @Mapping(target = "id", source = "socialMedia.id")
    @Mapping(target = "name_account", source = "socialMedia.name")
    @Mapping(target = "nickname", source = "socialMedia.nickname")
    @Mapping(target = "link", source = "socialMedia.link")
    @Mapping(target = "avatar", source = "socialMedia.avatar")
    @Mapping(target = "platform", source = "socialMedia.platform")
    SocialMediaResponse toResponse(SocialMedia socialMedia);

}
