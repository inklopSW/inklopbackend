package com.inklop.inklop.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inklop.inklop.repositories.SubmissionRepository;
import com.inklop.inklop.services.scrapper.ScrapperService;
import com.inklop.inklop.services.scrapper.dto.VideoStatsResponse;
import com.inklop.inklop.controllers.submission.request.AppealRequest;
import com.inklop.inklop.controllers.submission.request.AppealStatusRequest;
import com.inklop.inklop.controllers.submission.response.AppealResponse;

import jakarta.transaction.Transactional;
import com.inklop.inklop.mappers.AppealMapper;
import com.inklop.inklop.entities.Appeal;
import com.inklop.inklop.entities.Submission;
import com.inklop.inklop.entities.valueObject.submission.AppealStatus;
import com.inklop.inklop.entities.valueObject.submission.SubmissionStatus;
import com.inklop.inklop.entities.valueObject.submission.TypeAppeal;
import com.inklop.inklop.repositories.AppealRepository;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppealService {
    private final AppealRepository appealRepository;
    private final AppealMapper appealMapper;
    private final ScrapperService scrapperService;
    private final SubmissionRepository submissionRepository;

    @Transactional
    public AppealResponse createAppealBusinesstoCreator(AppealRequest appealRequest) {
        Submission submission = submissionRepository.findById(appealRequest.submissionId()).orElseThrow(() -> new RuntimeException("Submission no encontrada con id " + appealRequest.submissionId()));
        if (submission.isFinalStatus()){
            throw new RuntimeException("No se puede crear una appeal para una submission en estado final");
        }
        Appeal appeal = new Appeal();
        appeal.setSubmission(submission);
        appeal.setToCreator(true);
        appeal.setReason(appealRequest.reason());
        appeal.setTypeAppeal(appealRequest.typeAppeal());
        if (appealRequest.typeAppeal() == TypeAppeal.VIDEO_NOT_APPROVED_BY_AI) {
            appeal.setTypeAppeal(TypeAppeal.OTHER);
        }
        submission.setSubmissionStatus(SubmissionStatus.ON_APPEAL);
        submissionRepository.save(submission);
        appeal = appealRepository.save(appeal);

        return appealMapper.toAppealResponse(appeal, null);
    }

    @Transactional
    public AppealResponse createAppealCreatortoBusiness(AppealRequest appealRequest) {
        Submission submission = submissionRepository.findById(appealRequest.submissionId()).orElseThrow(() -> new RuntimeException("Submission no encontrada con id " + appealRequest.submissionId()));
        if (submission.isFinalStatus()){
            throw new RuntimeException("No se puede crear una appeal para una submission en estado final");
        }
        Appeal appeal = new Appeal();
        appeal.setSubmission(submission);
        appeal.setToCreator(false);
        appeal.setReason(appealRequest.reason());
        appeal.setTypeAppeal(TypeAppeal.VIDEO_NOT_APPROVED_BY_AI);
        submission.setSubmissionStatus(SubmissionStatus.ON_APPEAL);
        submissionRepository.save(submission);
        appeal = appealRepository.save(appeal);

        return appealMapper.toAppealResponse(appeal, null);
    }

    @Transactional
    public List<AppealResponse> getAllAppealsByStatus(AppealStatus status) {
        List<Appeal> appeals = appealRepository.findByAppealStatusOrderByUpdatedAtAsc(status);
        List<String> videoUrls = appeals.stream()
            .map(appeal -> appeal.getSubmission().getVideoUrl())
            .toList();
        Map<String, VideoStatsResponse> videoStatsMap = scrapperService.getAllPosts(videoUrls);
        return appeals.stream()
            .map(appeal -> appealMapper.toAppealResponse(appeal, videoStatsMap.get(appeal.getSubmission().getVideoUrl())))
            .toList();
    }

    @Transactional
    public AppealResponse setAppealStatus(Long id, AppealStatusRequest appealStatusRequest) {
        Appeal appeal = appealRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appeal no encontrada con id " + id));
        Submission submission = appeal.getSubmission();

        AppealStatus current = appeal.getAppealStatus();
        AppealStatus target = appealStatusRequest.status();

        // Logica de cambio de estado
        if (current == AppealStatus.APPROVED || current == AppealStatus.REJECTED) {
            throw new RuntimeException("Appeal ya ha sido procesada");
        }

        // From PENDING to IN_PROGRESS
        if (current == AppealStatus.PENDING) {
            if (target == AppealStatus.IN_REVIEW) {
                appeal.setAppealStatus(AppealStatus.IN_REVIEW);
                appeal.setAdminComment("Video under review");
            }
            if (target == AppealStatus.CANCELED) {
                appeal.setAppealStatus(AppealStatus.CANCELED);
                appeal.setAdminComment("Video appeal canceled by user request");
                if (appeal.getToCreator()) {
                    submission.setSubmissionStatus(SubmissionStatus.FINAL_REJECTED);
                } else {
                    submission.setSubmissionStatus(SubmissionStatus.FINAL_APPROVED);
                }
                submissionRepository.save(submission);
            }
        }

        // From IN_REVIEW to APPROVED or REJECTED
        if (current == AppealStatus.IN_REVIEW){
            if (target == AppealStatus.APPROVED) {
                if (submissionRepository.existsBySavedVideoUrl(appeal.getSubmission().getVideoUrl())){
                    appeal.setAppealStatus(AppealStatus.REJECTED);
                    appeal.setAdminComment("Video has already been submitted");
                    submission.setSubmissionStatus(SubmissionStatus.FINAL_REJECTED);
                    submissionRepository.save(submission);
                } else {
                    appeal.setAppealStatus(AppealStatus.APPROVED);
                    appeal.setAdminComment(appealStatusRequest.adminComment());
                    submission.setSubmissionStatus(SubmissionStatus.FINAL_APPROVED);
                    submissionRepository.save(submission);
                }
            }

            if (target == AppealStatus.REJECTED) {
                appeal.setAppealStatus(AppealStatus.REJECTED);
                appeal.setAdminComment(appealStatusRequest.adminComment());
                submission.setSubmissionStatus(SubmissionStatus.FINAL_REJECTED);
                submissionRepository.save(submission);
            }
        }

        appeal = appealRepository.save(appeal);

        return appealMapper.toAppealResponse(appeal, null);
    }
}
