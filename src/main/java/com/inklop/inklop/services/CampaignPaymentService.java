package com.inklop.inklop.services;

import org.springframework.stereotype.Service;

import com.inklop.inklop.controllers.campaign.request.FullBankPayment;
import com.inklop.inklop.entities.Campaign.BankTransfer;
import com.inklop.inklop.entities.Campaign.Campaign;
import com.inklop.inklop.entities.Campaign.CampaignPayment;
import com.inklop.inklop.entities.valueObject.campaign.PaymentType;
import com.inklop.inklop.repositories.Campaign.BankTransferRepository;
import com.inklop.inklop.repositories.Campaign.CampaignPaymentRepository;
import com.inklop.inklop.repositories.Campaign.CampaignRepository;

@Service
public class CampaignPaymentService {
    CampaignRepository campaignRepository;
    CampaignPaymentRepository campaignPaymentRepository;
    BankTransferRepository bankTransferRepository;

    public CampaignPaymentService(CampaignPaymentRepository campaignPaymentRepository, BankTransferRepository bankTransferRepository, CampaignPaymentRepository campaignPayment){
        this.campaignPaymentRepository=campaignPaymentRepository;
        this.bankTransferRepository=bankTransferRepository;
        this.campaignPaymentRepository=campaignPayment;
    }

    public CampaignPayment createCampaignPayment_Transfer(FullBankPayment fullBankPayment){
        CampaignPayment campaignPayment = new CampaignPayment();
        Campaign campaign = campaignRepository.findById(fullBankPayment.campaignId()).orElseThrow(
            ()-> new IllegalArgumentException("Campaign not found")
        );
        campaignPayment.setCampaign(campaign);
        campaignPayment.setBusinessName(fullBankPayment.businessName());
        campaignPayment.setRUC(fullBankPayment.RUC());
        campaignPayment.setAmount(campaign.getTotalBudget());
        campaignPayment.setPaymentType(PaymentType.BANK_TRANSFER);
        campaignPayment.setCurrency(campaign.getCurrency());
        campaignPayment = campaignPaymentRepository.save(campaignPayment);

        // transfer details
        // aclaracion por ahora no existe el tema de descuentos
        BankTransfer bankTransfer= new BankTransfer();
        bankTransfer.setCampaignPayment(campaignPayment);
        bankTransfer.setBankName(fullBankPayment.bankName());
        bankTransfer.setOperationNumber(fullBankPayment.operationNumber());
        bankTransfer.setAmmount(campaign.getTotalBudget());

        bankTransferRepository.save(bankTransfer);
        return campaignPayment;

    }
}
