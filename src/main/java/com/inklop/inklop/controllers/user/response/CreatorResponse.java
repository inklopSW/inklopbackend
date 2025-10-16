package com.inklop.inklop.controllers.user.response;

import java.util.List;

import com.inklop.inklop.entities.valueObject.user.CreatorCategory;
import com.inklop.inklop.entities.valueObject.user.CreatorType;

public record CreatorResponse(
    String username,
    String avatar,
    String description,
    CreatorType creatorType,
    List<CreatorCategoryResponse> categories

) {
    public record CreatorCategoryResponse(
        CreatorCategory category
    ) {}
}
