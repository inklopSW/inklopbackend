package com.inklop.inklop.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.inklop.inklop.entities.Campaign.Campaign;
import com.inklop.inklop.entities.Campaign.CampaignPayment;
import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.PaymentType;
import com.inklop.inklop.repositories.Campaign.CampaignPaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignPaymentService {
    private final CampaignPaymentRepository campaignPaymentRepository;

    public CampaignPayment saveCampaignPayment(Campaign campaign, BigDecimal amount, String description,Currency currency, String RUC, String businessName, PaymentType paymentType) {
        CampaignPayment campaignPayment = new CampaignPayment();
        campaignPayment.setCampaign(campaign);
        campaignPayment.setAmount(amount);
        campaignPayment.setCurrency(currency);
        campaignPayment.setRUC(RUC);
        campaignPayment.setDescription(description);
        campaignPayment.setBusinessName(businessName);
        campaignPayment.setPaymentType(paymentType);
        return campaignPaymentRepository.save(campaignPayment);
    }
}
