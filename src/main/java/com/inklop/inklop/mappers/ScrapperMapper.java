package com.inklop.inklop.mappers;

import org.mapstruct.Mapper;

import com.inklop.inklop.services.scrapper.dto.PostResponse;
import com.inklop.inklop.services.scrapper.dto.VideoStatsResponse;

@Mapper(componentModel = "spring")
public interface ScrapperMapper {
    VideoStatsResponse toVideoStatsResponse(PostResponse postResponse);
}
