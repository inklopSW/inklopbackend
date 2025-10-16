package com.inklop.inklop.controllers.submission;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import com.inklop.inklop.controllers.submission.request.SimpleSubmissionRequest;

import com.inklop.inklop.controllers.submission.response.SubmissionResponse;
import com.inklop.inklop.controllers.submission.response.metrics.MetricsBusinessResponse;
import com.inklop.inklop.controllers.submission.response.metrics.MetricsCampaignResponse;
import com.inklop.inklop.controllers.submission.response.metrics.MetricsCreatorResponse;
import com.inklop.inklop.controllers.submission.response.SubmissionPaymentResponse;

import com.inklop.inklop.services.SubmisionService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;






@RestController
@RequestMapping("/submissions")
public class SubmissionController {
    private final SubmisionService submisionService;

    public SubmissionController(SubmisionService submisionService){
        this.submisionService=submisionService;
    }

    @PostMapping
    public ResponseEntity<SubmissionResponse> createSubmission(@RequestBody SimpleSubmissionRequest submission){
        return ResponseEntity.ok(submisionService.saveSubmission(submission));
    }
    
    /* 
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<ShowFullSubmission>> getAllSubmissionsByCreatorId(@PathVariable Long creatorId) throws Exception {
        return ResponseEntity.ok(submisionService.getAllSubmissionsByCreatorId(creatorId));
    }*/

    
    @GetMapping("/metrics/campaign/{campaignId}")
    public ResponseEntity<MetricsCampaignResponse> getMetricsByCampaignId(@PathVariable Long campaignId) throws Exception {
        return ResponseEntity.ok(submisionService.getMetricsByCampaignId(campaignId));
    }

    @GetMapping("/metrics/bussiness/{bussinessId}")
    public ResponseEntity<MetricsBusinessResponse> getBussienssMetrics(@PathVariable Long bussinessId) throws Exception{
        return ResponseEntity.ok(submisionService.getMetricsBussiness(bussinessId));
    }

    @GetMapping("/metrics/creator/{creatorId}")
    public ResponseEntity<MetricsCreatorResponse> getCreatorMetrics(@PathVariable Long creatorId) throws Exception{
        return ResponseEntity.ok(submisionService.getMetricsCreator(creatorId));
    } 
    

    @GetMapping("/payment/{submissionId}")
    public ResponseEntity<SubmissionPaymentResponse> getPayment(@PathVariable Long submissionId) throws Exception{
        return ResponseEntity.ok(submisionService.getPayment(submissionId));
    }
    

}
