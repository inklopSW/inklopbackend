package com.inklop.inklop.controllers.submission;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inklop.inklop.controllers.submission.request.AppealRequest;
import com.inklop.inklop.controllers.submission.request.AppealStatusRequest;
import com.inklop.inklop.controllers.submission.response.AppealResponse;
import com.inklop.inklop.entities.valueObject.submission.AppealStatus;
import com.inklop.inklop.services.AppealService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/appeals")
public class AppealController {
    private final AppealService appealService;

    public AppealController(AppealService appealService) {
        this.appealService = appealService;
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppealResponse>> getAppealsByStatus(@PathVariable AppealStatus status) {
        return ResponseEntity.ok(appealService.getAllAppealsByStatus(status));
    }

    @PostMapping("/toCreator")
    public ResponseEntity<AppealResponse> sendAppealToCreator(@RequestBody AppealRequest appealStatusRequest) {
        return ResponseEntity.ok(appealService.createAppealBusinesstoCreator(appealStatusRequest));
    }

    @PostMapping("/toBusiness")
    public ResponseEntity<AppealResponse> sendAppealToBusiness(@RequestBody AppealRequest appealStatusRequest) {
        return ResponseEntity.ok(appealService.createAppealCreatortoBusiness(appealStatusRequest));
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<AppealResponse> updateAppealStatus(@PathVariable Long id, @RequestBody AppealStatusRequest appealStatusRequest) {
        return ResponseEntity.ok(appealService.setAppealStatus(id, appealStatusRequest));
    }



}
