package com.inklop.inklop.controllers.campaign;


import com.inklop.inklop.controllers.campaign.response.FullCampaignResponse;
import com.inklop.inklop.controllers.campaign.response.ShortCampaignResponse;
import com.inklop.inklop.controllers.campaign.response.TransaccionComplete;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.inklop.inklop.services.CampaignService;
import com.inklop.inklop.entities.Campaign.Campaign;
import com.inklop.inklop.controllers.campaign.request.AddCampaignRequest;
import com.inklop.inklop.controllers.campaign.request.CampaignFullRequest;
import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;
import com.inklop.inklop.entities.valueObject.campaign.CampaignStatus;

import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/campaign")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/full")
    public ResponseEntity<TransaccionComplete> createFullCampaign(@RequestBody CampaignFullRequest request){
        return ResponseEntity.ok(campaignService.createFullCampaign(request));
    }

    @PutMapping("/addOn/{id}")
    public ResponseEntity<TransaccionComplete> updateFullCampaign(
        @PathVariable Long id,
        @RequestBody AddCampaignRequest request){
        return ResponseEntity.ok(campaignService.addOnCampaign(id, request));
    }

    @GetMapping("/short/{status}")
    public ResponseEntity<List<ShortCampaignResponse>> getAllShortCampaigns(@PathVariable CampaignStatus status){
        return ResponseEntity.ok(campaignService.getAllShortCampaigns(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullCampaignResponse> getCampaignById(@PathVariable Long id){
        return ResponseEntity.ok(campaignService.getFullCampaignById(id));
    }

    @GetMapping("/bussiness/{id}")
    public ResponseEntity<List<ShortCampaignResponse>> getAllShotCampaignsbyBusiness(@PathVariable Long id){
        return ResponseEntity.ok(campaignService.getAllShotCampaignsbyBusiness(id));
    }


    @PutMapping("/payment/status/{id}")
    public ResponseEntity<Campaign> setCampaignPaymentStatus(
        @PathVariable Long id,
        @RequestParam PaymentStatus paymentStatus) {
        return ResponseEntity.ok(campaignService.setCampaignPaymentStatus(id, paymentStatus));
    }

    
}
