package com.inklop.inklop.controllers.submission.response.metrics;

import java.util.List;

import com.inklop.inklop.controllers.submission.response.ShowFullSubmission;

public record MetricsSimple(
    Long views,
    Long likes,
    Long comments,
    Long shareCount,
    Integer quantity,
    Long viewsFb,
    Long viewsTk,
    Long viewsIg,
    List<ShowFullSubmission> submissions
) {

}
