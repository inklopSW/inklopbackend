package com.inklop.inklop.mappers;
import com.inklop.inklop.controllers.submission.response.AppealResponse;
import com.inklop.inklop.entities.Appeal;
import com.inklop.inklop.services.scrapper.dto.VideoStatsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AppealMapper {
    @Mapping(target = "videoStats", source = "videoStatsResponse")
    AppealResponse toAppealResponse(Appeal appeal, VideoStatsResponse videoStatsResponse);
}
