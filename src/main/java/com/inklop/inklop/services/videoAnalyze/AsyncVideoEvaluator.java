package com.inklop.inklop.services.videoAnalyze;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.inklop.inklop.controllers.webSocket.response.NotificationResponse;
import com.inklop.inklop.entities.Submission;
import com.inklop.inklop.entities.valueObject.submission.SubmissionStatus;
import com.inklop.inklop.repositories.SubmissionRepository;
import com.inklop.inklop.services.videoAnalyze.dto.VideoAnalyzeResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsyncVideoEvaluator {
    private final SubmissionRepository submissionRepository;
    private final VideoAnalyzeService videoAnalyzeService;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @Transactional
    public void evaluateSubmissionAsync(Long id) {
        Submission submission = submissionRepository.findById(id).orElseThrow();

        try {

            VideoAnalyzeResponse response = videoAnalyzeService.analyzeVideo(
                submission.getCampaign().getId(),
                submission.getVideoUrl(),
                submission.getCampaign().getEndDate(),
                submission.getCampaign().getMaxDescription()
            );

            Integer percentage = response.alignment().match_percent().intValue();

            submission.setPercentage(percentage);

            if (percentage > 69) {
                submission.setSubmissionStatus(SubmissionStatus.APPROVED);
                submission.setDescription("Si cumple con los lineamientos");
                submission.setSavedVideoUrl(submission.getVideoUrl());
            } else {
                submission.setSubmissionStatus(SubmissionStatus.REJECTED);
                submission.setDescription("No cumple con los lineamientos");
            }

            submissionRepository.save(submission);

            NotificationResponse noti = new NotificationResponse(
                    submission.getCampaign().getLogo(),
                    submission.getSubmissionStatus().equals(SubmissionStatus.APPROVED)
                        ? "Tu publicación ha sido aprobada"
                        : "Tu publicación ha sido rechazada",
                    "La publicación de tu video ha sido evaluada en la campaña " + submission.getCampaign().getName(),
                    LocalDateTime.now(ZoneId.of("America/Lima"))
            );

            messagingTemplate.convertAndSend("/topic/newSubmission" + submission.getSocialMedia().getUser().getId(), noti);

        } catch (Exception e) {
            e.printStackTrace();
            submission.setDescription("Error al evaluar el video, por favor intenta nuevamente");
            submission.setSubmissionStatus(SubmissionStatus.PENDING);
            submissionRepository.save(submission);
        }
    }

}
