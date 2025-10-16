package com.inklop.inklop.services;

import com.inklop.inklop.repositories.BusinessRepository;
import com.inklop.inklop.repositories.WalletRepository;
import com.inklop.inklop.controllers.campaign.response.FullCampaignResponse.SocialMediaDto;
import com.inklop.inklop.controllers.webSocket.response.NotificationResponse;
import com.inklop.inklop.controllers.campaign.request.AddCampaignRequest;
import com.inklop.inklop.controllers.campaign.request.CampaignFullRequest;
import com.inklop.inklop.controllers.campaign.response.FullCampaignResponse;
import com.inklop.inklop.controllers.campaign.response.ShortCampaignResponse;
import com.inklop.inklop.controllers.campaign.response.TransaccionComplete;
import com.inklop.inklop.repositories.Campaign.*;

import lombok.RequiredArgsConstructor;

import com.inklop.inklop.entities.SocialMedia;
import com.inklop.inklop.entities.Wallet;
import com.inklop.inklop.entities.Campaign.*;
import com.inklop.inklop.entities.valueObject.campaign.*;
import com.inklop.inklop.entities.valueObject.user.CreatorType;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignCategoryRepository campaignCategoryRepository;
    private final CampaignCountryRepository campaignCountryRepository;
    private final CampaignRequirementsRepository campaignRequirementsRepository;
    private final BusinessRepository businessRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WalletRepository walletRepository;
    private final RoomService roomService;


    private String getSpanishDate(LocalDate date) {
        return date.getDayOfMonth()+" de "+date.getMonth().getDisplayName(TextStyle.FULL,Locale.of("es","ES"))+", "+date.getYear();
    }

    public void updateCampaignStatusesToday() {
        LocalDate today = LocalDate.now();
        List<Campaign> campaigns = campaignRepository.findByStatusAndDateBeforeOrEqual(
            List.of(CampaignStatus.IN_COMING, CampaignStatus.IN_PROGRESS),today);
        List<Campaign> updatedCampaigns = new ArrayList<>();
        for (Campaign campaign : campaigns) {
            if (campaign.getEndDate().isBefore(today) || campaign.getEndDate().isEqual(today)) {
                campaign.setCampaignStatus(CampaignStatus.COMPLETED);
                BigDecimal total = campaign.getTotalBudget().min(campaign.getConsumedBudget());
                Wallet wallet= walletRepository.findByUserId(campaign.getBusiness().getUser().getId()).get();
                if (campaign.getCurrency().equals(Currency.USD)){
                    wallet.setUSD(wallet.getUSD().add(total));
                } else if (campaign.getCurrency().equals(Currency.PEN)){
                    wallet.setPEN(wallet.getPEN().add(total));
                }   
                walletRepository.save(wallet);

            } else if (campaign.getStartDate().isBefore(today) || campaign.getStartDate().isEqual(today)) {
                campaign.setCampaignStatus(CampaignStatus.IN_PROGRESS);
            }
            updatedCampaigns.add(campaign);
        }
        campaignRepository.saveAll(updatedCampaigns);
    }


    public TransaccionComplete createFullCampaign(CampaignFullRequest request){
        Campaign campaign = new Campaign();
        campaign.setName(request.title());
        campaign.setDescription(request.description());
        campaign.setType(request.creatorType());
        campaign.setLogo(request.logo());
        campaign.setStartDate(request.startDate());
        campaign.setEndDate(request.endDate()); 
        // Set the business associated with the campaign
        campaign.setBusiness(businessRepository.findByUser_Id(request.businessId())
            .orElseThrow(() -> new IllegalArgumentException("Business not found with id: " + request.businessId())));
        // url guia
        campaign.setTypeText(request.typeText());
        campaign.setTextInfluencer(request.textInfluencer());
        
        campaign.setCurrency(request.currency());
        campaign.setCpm(request.cpm());
        campaign.setTotalBudget(request.totalBudget());
        campaign.setMaximunPayment(request.maximunPayment());
        campaign.setMinimumPayment(request.minimumPayment());
        campaign.setHasTiktok(request.hasTiktok());
        campaign.setHasInstagram(request.hasInstagram());
        campaign.setHasFacebook(request.hasFacebook());
        // categorias
        campaign = campaignRepository.save(campaign);
        CampaignCategory category = new CampaignCategory();
        category.setCategory(request.category());
        category.setCampaign(campaign);
        //obtener las categorias
        campaignCategoryRepository.save(category);
        // countries
        List<CampaignCountry> countries = new ArrayList<>();
        for (CampaignFullRequest.CountryDto countryDto : request.countries()) {
            CampaignCountry country = new CampaignCountry();
            country.setCountry(countryDto.country());
            country.setDepartment(countryDto.department());
            country.setCampaign(campaign);
            countries.add(country);
        }
        campaignCountryRepository.saveAll(countries);
        // requeriments
        List<CampaignRequirements> requeriments = new ArrayList<>();
        for (CampaignFullRequest.RequerimentDto requerimentDto : request.requeriments()) {
            CampaignRequirements requeriment = new CampaignRequirements();
            requeriment.setRequirement(requerimentDto.requeriment());
            requeriment.setCampaign(campaign);
            requeriments.add(requeriment);
        }
        campaignRequirementsRepository.saveAll(requeriments);

        setCampaignPaymentStatus(campaign.getId(), PaymentStatus.APPROVED);

        NotificationResponse noti = new NotificationResponse(
            request.logo(),
            "Salio la nueva campaña de "+request.title(),
            "Aprovecha esta campaña que estara disponible desde el " + getSpanishDate(request.startDate())+ "hasta el " + getSpanishDate(request.endDate())+".",
            LocalDateTime.now(ZoneId.of("America/Lima")) 
        );

        messagingTemplate.convertAndSend("/topic/newCampaigns/creators", noti);

        roomService.createBothRooms(campaign);
        return new TransaccionComplete(
            campaign.getName(),
            campaign.getTotalBudget(),
            campaign.getCurrency(),
            getSpanishDate(LocalDate.now()),
            PaymentStatus.APPROVED,
            "INK-2025-00TEST"
        );


    }

    //metodo de prueba eligan lo que gustan
    


    public TransaccionComplete addOnCampaign(Long campaignId, AddCampaignRequest request){
        Campaign campaign = campaignRepository.findById(campaignId).get();
        campaign.setTotalBudget(campaign.getTotalBudget().add(request.addOn()));
        campaign.setEndDate(campaign.getEndDate().plusDays(request.days()));
        campaignRepository.save(campaign);
        return new TransaccionComplete(
            campaign.getName(),
            request.addOn(),
            campaign.getCurrency(),
            getSpanishDate(LocalDate.now()),
            PaymentStatus.APPROVED,
            "INK-2025-00TEST"
        );
    }

    public Campaign setCampaignStatus(Long campaignId, CampaignStatus status){
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Campaign not found with id: " + campaignId));
        
        if (status == CampaignStatus.IN_COMING || status == CampaignStatus.IN_PROGRESS || status == CampaignStatus.COMPLETED) {
            throw new IllegalStateException(
            "Cannot manually set status to " + status + ". Status is managed automatically based on payment and dates."
        );
        }

        campaign.setCampaignStatus(status);
        return campaignRepository.save(campaign);
    }

    //supuestamente sera uso unico cuando se apruebe el pago o no
    public Campaign setCampaignPaymentStatus(Long campaignId, PaymentStatus status){
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Campaign not found with id: " + campaignId));
        campaign.setPaymentStatus(status);
        if (status == PaymentStatus.APPROVED) {
            if (campaign.getStartDate().isAfter(LocalDate.now())) {
                campaign.setCampaignStatus(CampaignStatus.IN_COMING);
            } else {
                campaign.setCampaignStatus(CampaignStatus.IN_PROGRESS);
            }
        } else if (status == PaymentStatus.REJECTED) {
            campaign.setCampaignStatus(CampaignStatus.REJECTED);
        }
        return campaignRepository.save(campaign);
    }
    

    public List<ShortCampaignResponse> getAllShortCampaigns(CampaignStatus status) {
        List<Campaign> campaigns = campaignRepository.findByStatus(status);
        List<ShortCampaignResponse> shortCampaings = new ArrayList<>();
        for (Campaign campaign: campaigns) {
            String typeAux, category;
            
            if (campaign.getType().equals(CreatorType.UGC)){
                typeAux="RECOMENDACION";
            }else {
                typeAux="CLIPPING";
            }

            BigDecimal percentage = campaign.getConsumedBudget()
                .divide(campaign.getTotalBudget(),4,RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(0,RoundingMode.DOWN);


            category = campaignCategoryRepository.findAllByCampaign_Id(campaign.getId()).getFirst().getCategory().toString();
            shortCampaings.add(new ShortCampaignResponse(
               campaign.getId(),
               campaign.getName(),
               campaign.getLogo(),
               campaign.getCampaignStatus(),
               campaign.getCpm(),
               campaign.getTotalBudget(),
               campaign.getConsumedBudget(),
               percentage.intValue(),
               typeAux,
               category,
               campaign.getHasTiktok(),
               campaign.getHasInstagram(),
               campaign.getHasFacebook(),
               campaign.getStartDate(),
               campaign.getBusiness().getBusinessName()
            ));
        }

        
        return shortCampaings;

    }

    public List<ShortCampaignResponse> getAllShotCampaignsbyBusiness(Long id) {
        List<Campaign> campaigns = campaignRepository.findByBusinessId(id);
        List<ShortCampaignResponse> shortCampaings = new ArrayList<>();
        for (Campaign campaign: campaigns) {
            String typeAux, category;
            
            if (campaign.getType().equals(CreatorType.UGC)){
                typeAux="RECOMENDACION";
            }else {
                typeAux="CLIPPING";
            }

            category = campaignCategoryRepository.findAllByCampaign_Id(campaign.getId()).getFirst().getCategory().toString();

            BigDecimal percentage = campaign.getConsumedBudget()
                .divide(campaign.getTotalBudget(),4,RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(0,RoundingMode.DOWN);

            shortCampaings.add(new ShortCampaignResponse(
               campaign.getId(),
               campaign.getName(),
               campaign.getLogo(),
               campaign.getCampaignStatus(),
               campaign.getCpm(),
               campaign.getTotalBudget(),
               campaign.getConsumedBudget(),
               percentage.intValue(),
               typeAux,
               category,
               campaign.getHasTiktok(),
               campaign.getHasInstagram(),
               campaign.getHasFacebook(),
               campaign.getStartDate(),
               campaign.getBusiness().getBusinessName()
            ));
        }

        return shortCampaings;

    }



    public FullCampaignResponse getFullCampaignById(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).get();
        Integer durationinDays = (int)ChronoUnit.DAYS.between(campaign.getStartDate(), campaign.getEndDate());
        String typeAux;
        if (campaign.getType().equals(CreatorType.UGC)){
                typeAux="RECOMENDACION";
            }else {
                typeAux="CLIPPING";
            }

        BigDecimal percentage = campaign.getConsumedBudget()
                .divide(campaign.getTotalBudget(),4,RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(0,RoundingMode.DOWN);
        
        String category = campaign.getCampaignCategories().getFirst().getCategory().toString();
        Integer maximum_views= campaign.getMaximunPayment().divide(campaign.getCpm(),4,RoundingMode.HALF_UP).multiply(new BigDecimal(1000)).intValue();

        List<String> guidelines= new ArrayList<>();

        for (CampaignRequirements campaignRequirement : campaign.getCampaignRequirements()){
            guidelines.add(campaignRequirement.getRequirement());
        }

        List<SocialMediaDto> socialMediaDtos = new ArrayList<>();

        for (SocialMedia socialMedia: campaign.getBusiness().getUser().getSocialMedias()){
            SocialMediaDto socialMediaDto = new SocialMediaDto(
                socialMedia.getPlatform(),
                socialMedia.getLink());

            socialMediaDtos.add(socialMediaDto);
        }

        List<FullCampaignResponse.ubicationDto> ubications = new ArrayList<>();

        for (CampaignCountry country: campaign.getCampaignCountries()){
            FullCampaignResponse.ubicationDto ubication = new FullCampaignResponse.ubicationDto(
                country.getCountry(),
                country.getDepartment()
            );
            ubications.add(ubication);
        }

        return new FullCampaignResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getLogo(),
            campaign.getDescription(),
            durationinDays,
            typeAux,
            category,
            //social
            campaign.getHasTiktok(),
            campaign.getHasInstagram(),
            campaign.getHasFacebook(),
            // progresoo
            percentage.intValue(),
            campaign.getTotalBudget(),
            campaign.getConsumedBudget(),
            // CPM PAGO MAX
            campaign.getCpm(),
            campaign.getCurrency(),
            campaign.getMinimumPayment(),
            campaign.getMaximunPayment(),
            maximum_views,
            //guidelines
            guidelines,
            campaign.getTypeText(),
            campaign.getTextInfluencer(),
            socialMediaDtos,
            ubications,
            new FullCampaignResponse.BusinessDto(
                campaign.getBusiness().getId(),
                campaign.getBusiness().getBusinessName(),
                campaign.getBusiness().getAvatarBusiness(),
                campaign.getBusiness().getSector()
            ));
        
    }

    public BigDecimal getMaxPaymentAndBudget(Campaign campaign, BigDecimal payment) {
        BigDecimal availableBudget = campaign.getTotalBudget().subtract(campaign.getConsumedBudget());
        BigDecimal maximumPayment = campaign.getMaximunPayment();
        return payment.min(availableBudget).min(maximumPayment);
    }
        

}
