package com.inklop.inklop.services;

import org.springframework.stereotype.Service;

import com.inklop.inklop.repositories.Campaign.BankTransferRepository;
import com.inklop.inklop.repositories.Campaign.CampaignPaymentRepository;

@Service
public class BankTransferService {
    CampaignPaymentRepository campaignPaymentRepository;
    BankTransferRepository bankTransferRepository;

    public BankTransferService(CampaignPaymentRepository campaignPaymentRepository, BankTransferRepository bankTransferRepository){
        this.campaignPaymentRepository=campaignPaymentRepository;
        this.bankTransferRepository=bankTransferRepository;
    }

    


}
