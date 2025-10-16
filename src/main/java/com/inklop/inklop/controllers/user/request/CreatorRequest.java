package com.inklop.inklop.controllers.user.request;

import com.inklop.inklop.entities.valueObject.user.CreatorCategory;
import com.inklop.inklop.entities.valueObject.user.CreatorType;
import java.util.List;

public record CreatorRequest(
        //user
        UserRegisterRequest user,
        //creatoR
        String username, //all in minuscule and without spaces
        CreatorType creatorType,
        String description,
        String avatar,
        List<CreatorCategory> categories,
        //socialMedia
        List<SocialMediaRequest> socialMedias
) {
}
