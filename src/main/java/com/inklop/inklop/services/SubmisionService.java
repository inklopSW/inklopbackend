package com.inklop.inklop.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.inklop.inklop.controllers.submission.request.SimpleSubmissionRequest;
import com.inklop.inklop.controllers.submission.response.SubmissionResponse;
import com.inklop.inklop.controllers.submission.response.SubmissionPaymentResponse;
import com.inklop.inklop.controllers.webSocket.response.NotificationResponse;
import com.inklop.inklop.controllers.submission.response.metrics.MetricsBusinessResponse;
import com.inklop.inklop.controllers.submission.response.metrics.MetricsCampaignResponse;
import com.inklop.inklop.controllers.submission.response.metrics.MetricsCreatorResponse;
import com.inklop.inklop.controllers.submission.response.metrics.MetricsSimple;
import com.inklop.inklop.controllers.submission.response.metrics.MetricsCreatorResponse.IncomesResponse;
import com.inklop.inklop.controllers.submission.response.IncomeDto;
import com.inklop.inklop.controllers.submission.response.ShowFullSubmission;
import com.inklop.inklop.entities.SocialMedia;
import com.inklop.inklop.entities.Submission;
import com.inklop.inklop.entities.SubmissionPayment;
import com.inklop.inklop.entities.Wallet;
import com.inklop.inklop.entities.Campaign.Campaign;
import com.inklop.inklop.entities.valueObject.campaign.CampaignStatus;
import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;
import com.inklop.inklop.entities.valueObject.submission.SubmissionStatus;
import com.inklop.inklop.entities.valueObject.user.Platform;
import com.inklop.inklop.repositories.SocialMediaRepository;
import com.inklop.inklop.repositories.SubmissionRepository;
import com.inklop.inklop.repositories.UserRepository;
import com.inklop.inklop.repositories.SubmissionPaymentRepository;
import com.inklop.inklop.repositories.WalletRepository;
import com.inklop.inklop.repositories.Campaign.CampaignRepository;
import com.inklop.inklop.services.scrapper.ScrapperService;
import com.inklop.inklop.services.scrapper.dto.ValueVideo;
import com.inklop.inklop.services.scrapper.dto.VideoStatsResponse;
import com.inklop.inklop.entities.valueObject.Status;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmisionService {
    private final SubmissionRepository submisionRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final CampaignService campaignService;
    private final SubmissionPaymentRepository submissionPaymentRepository;
    private final WalletRepository walletRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SocialMediaRepository socialMediaRepository;
    private final ScrapperService scrapperService;

    public SubmissionResponse saveSubmission(SimpleSubmissionRequest submissionRequest){
        Campaign campaign=campaignRepository.findById(submissionRequest.idCampaign()).get();
        if(!campaign.getCampaignStatus().equals(CampaignStatus.IN_PROGRESS)){
            throw new RuntimeException("Campaign is not in progress");
        }

        SocialMedia socialMedia=socialMediaRepository.findById(submissionRequest.idSocialMedia()).get();
        if (socialMedia.getUser().getUserRole().toString().equals("BUSINESS")){
            throw new RuntimeException("Submission not allowed for business users");
        }
        if (socialMedia.getStatus().equals(Status.INACTIVE)){
            throw new RuntimeException("Social media is inactive");
        }

        ValueVideo videoInfo = scrapperService.postVideoToExternalApi(submissionRequest.videoUrl(), socialMedia.getPlatform(), campaign.getEndDate());
        
        Submission submission = new Submission();
        submission.setCampaign(campaign);
        submission.setSocialMedia(socialMedia);
        submission.setVideoUrl(videoInfo.videoUrl());
        //aca esta lo que es un porcentaje falso ahora los videos seran de prueba 
        submission.setPercentage(71);

        if ( !videoInfo.ownerId().equals(socialMedia.getOwnerId())){
            submission.setDescription("video no aprobado x que no coincide con el id");
            submission.setSubmissionStatus(SubmissionStatus.REJECTED);
            submisionRepository.save(submission);


            return new SubmissionResponse(
                submission.getId(),
                submission.getVideoUrl(),
                submission.getSubmissionStatus(),
                submission.getPercentage(),
                submission.getDescription()
            );
        }
        
        if (submission.getPercentage()>69){
            NotificationResponse noti = new NotificationResponse(
            campaign.getLogo(),
            "Tu publicacion ha sido aprobada",
            "La publicacion de tu video a sido aprobado en la campaña" + campaign.getName(),
            LocalDateTime.now(ZoneId.of("America/Lima")) 
        );
            submission.setDescription("Si cumple con los lineamientos");
            submission.setSubmissionStatus(SubmissionStatus.APPROVED);
            submission.setSavedVideoUrl(videoInfo.videoUrl());
            messagingTemplate.convertAndSend("/topic/newSubmission"+submission.getSocialMedia().getUser().getId(), noti);
            
        } else {
            NotificationResponse noti = new NotificationResponse(
            campaign.getLogo(),
            "Tu publicacion ha sido rechazada",
            "La publicacion de tu video a sido rechazada en la campaña" + campaign.getName(),
            LocalDateTime.now(ZoneId.of("America/Lima")) 
        );
            submission.setDescription("No cumple con los lineamientos");
            submission.setSubmissionStatus(SubmissionStatus.REJECTED);
            messagingTemplate.convertAndSend("/topic/newSubmission"+submission.getSocialMedia().getUser().getId(), noti);
        }

        submisionRepository.save(submission);

        return new SubmissionResponse(
                submission.getId(),
                submission.getVideoUrl(),
                submission.getSubmissionStatus(),
                submission.getPercentage(),
                submission.getDescription()
        );

    }

    /* 
    public List<ShowFullSubmission> getAllSubmissionsByCreatorId(Long creatorId) throws Exception {
        
        List<Submission> submissions = submisionRepository.findBySocialMediaUserId(creatorId);

        // 1. Separar URLs que necesitan scraping
        List<String> urlsToScrape = submissions.stream()
                .filter(s -> s.getSubmissionPayment() == null)
                .map(Submission::getVideoUrl)
                .collect(Collectors.toList());

        // 2. Scraping de los que no tienen SubmissionPayment
        List<ApifyResponse> apifyResponses = apifyService.scrapeUrlsByPlatform(urlsToScrape);

        Map<String, ApifyResponse> apifyResponseMap = apifyResponses.stream()
                .collect(Collectors.toMap(ApifyResponse::getVideoUrl, Function.identity()));

        // 3. Crear ShowFullSubmission para todos
        List<ShowFullSubmission> fullSubmissions = submissions.stream()
                .map(submission -> {
                    if (submission.getSubmissionPayment() != null) {
                        // Ya tiene SubmissionPayment
                        return new ShowFullSubmission(
                                submission.getId(),
                                submission.getSubmissionStatus(),
                                submission.getSubmittedAt(),
                                new IncomeDto(
                                    BigDecimal.ZERO,
                                    submission.getCampaign().getCurrency()
                                ),
                                new ApifyResponse(
                                        submission.getId().toString(),
                                        submission.getPlatform().name(),
                                        submission.getSubmissionPayment().getTimestamp(),
                                        submission.getSubmissionPayment().getCaption(),
                                        submission.getSubmissionPayment().getLikes(),
                                        submission.getSubmissionPayment().getViews(),
                                        submission.getSubmissionPayment().getComments(),
                                        submission.getSubmissionPayment().getShareCount(),
                                        submission.getSavedVideoUrl(),
                                        submission.getSubmissionPayment().getDisplayUrl(),
                                        new AuthorMeta(
                                                submission.getSubmissionPayment().getAvatar(),
                                                submission.getSubmissionPayment().getName(),
                                                submission.getSubmissionPayment().getNickName(),
                                                submission.getSubmissionPayment().getProfileUrl()
                                        )
                                )
                        );
                    } else {
                        // Usar scraping si existe
                        ApifyResponse scraped = apifyResponseMap.get(submission.getVideoUrl());
                        return new ShowFullSubmission(
                                submission.getId(),
                                (scraped != null) ? SubmissionStatus.APPROVED : SubmissionStatus.PENDING,
                                submission.getSubmittedAt(),
                                new IncomeDto(
                                    BigDecimal.ZERO,
                                    submission.getCampaign().getCurrency()
                                ),
                                scraped
                        );
                    }
                })
                .sorted(Comparator.comparing(ShowFullSubmission::submittedAt).reversed()) // Orden descendente por fecha
                .collect(Collectors.toList());

        return fullSubmissions;
    }*/

    private MetricsSimple getAllSubmissionsCBC(Long id, String type)throws Exception{
        List<Submission> submissions = new ArrayList<>();
        Long views=0L;
        Long likes=0L;
        Long comments=0L;
        Long shareCount=0L;
        Integer quantity=0;
        Long viewsFb=0L;
        Long viewsTk=0L;
        Long viewsIg=0L;

        if(type.equals("creator")){
            submissions = submisionRepository.findBySocialMediaUserId(id);
        } else if(type.equals("campaign")){
            submissions = submisionRepository.findByCampaignId(id);
        } else {
            submissions = submisionRepository.findByCampaignBusinessId(id);
        }
        List<ShowFullSubmission> showFullSubmissions = new ArrayList<>();
        List<String> urls= submissions.stream().map(Submission::getVideoUrl).toList();
        

        Map<String, VideoStatsResponse> postsByUrl = scrapperService.getAllPosts(urls);

        for (Submission submission : submissions) {
            BigDecimal payment= new BigDecimal(0);

            VideoStatsResponse postResponse= postsByUrl.get(submission.getVideoUrl());
            // VALIDACION SI NO EXISTE LINK
            payment=submission.getCampaign().getCpm().multiply(new BigDecimal(postResponse.views()).divide(new BigDecimal(1000)));
            payment=campaignService.getMaxPaymentAndBudget(submission.getCampaign(), payment);
            
            if (submission.getSubmissionStatus().equals(SubmissionStatus.REJECTED)){
                payment=new BigDecimal(0);
            }

            if (submission.getSubmissionPayment() != null) {
                payment=submission.getSubmissionPayment().getPaymentReceived();
            }

            if (submission.getSubmissionStatus().equals(SubmissionStatus.APPROVED) || submission.getSubmissionStatus().equals(SubmissionStatus.PAYED)){
                if (submission.getSocialMedia().getPlatform().equals(Platform.INSTAGRAM)){
                viewsIg+=postResponse.views();
            } else if (submission.getSocialMedia().getPlatform().equals(Platform.FACEBOOK)){
                viewsFb+=postResponse.views();
            } else if(submission.getSocialMedia().getPlatform().equals(Platform.TIKTOK)){
                viewsTk+=postResponse.views();            }

            views+= postResponse.views();
            likes+= postResponse.likes();
            comments+= postResponse.comments();
            shareCount+= postResponse.shares();
            quantity+=1;
            }

            showFullSubmissions.add(new ShowFullSubmission(
                submission.getId(),
                submission.getSubmissionStatus(),
                submission.getSubmittedAt(),
                new IncomeDto(
                    payment,
                    submission.getCampaign().getCurrency(),
                    submission.getCampaign().getName(),
                    submission.getCampaign().getLogo()
                ),
                postResponse
            ));
        }

        return new MetricsSimple(
            views, 
            likes, 
            comments, 
            shareCount, 
            quantity,
            viewsFb,
            viewsTk,
            viewsIg, 
            showFullSubmissions
        );

    }
    //falta implementar aca
    public MetricsCampaignResponse getMetricsByCampaignId (Long campaignId) throws Exception{
        MetricsSimple metricsSimple = getAllSubmissionsCBC(campaignId,"campaign");
        Campaign campaign = campaignRepository.findById(campaignId).get();
        return new MetricsCampaignResponse(
            campaignId,
            campaign.getName(),
            campaign.getLogo(),
            campaign.getTotalBudget(),
            campaign.getConsumedBudget(),
            campaign.actualDays(),
            campaign.totalDays(),
            metricsSimple.quantity(),
            metricsSimple.views(),
            metricsSimple.likes(),
            metricsSimple.comments(),
            metricsSimple.shareCount(),
            metricsSimple.viewsFb(),
            metricsSimple.viewsTk(),
            metricsSimple.viewsIg(),
            getEngagement(metricsSimple.likes(),metricsSimple.views(),metricsSimple.comments()),
            metricsSimple.submissions()
        );
    }

    public MetricsBusinessResponse getMetricsBussiness(Long userId) throws Exception{
        Long bussinessId= userRepository.findById(userId).get().getBusiness().getId();
        List<Campaign> campaigns = campaignRepository.findByBusinessId(bussinessId);
        if (campaigns.isEmpty()){
            return new MetricsBusinessResponse(
                bussinessId,
                new BigDecimal(0),
                0L,
                0,
                new BigDecimal(0)
            );
        }
        BigDecimal totalBudget=new BigDecimal(0);
        for (Campaign campaign : campaigns) {
            totalBudget = totalBudget.add(campaign.getTotalBudget());
        }

        MetricsSimple metricsSimple = getAllSubmissionsCBC(bussinessId,"bussiness");
        if (metricsSimple.quantity()==0){
            return new MetricsBusinessResponse(
                bussinessId,
                totalBudget,
                0L,
                0,
                new BigDecimal(0)
            );
        }
        return new MetricsBusinessResponse(
            bussinessId,
            totalBudget,
            metricsSimple.views(),
            metricsSimple.quantity(),
            getEngagement(metricsSimple.likes(),metricsSimple.views(),metricsSimple.comments())
        );
    }

    private BigDecimal getEngagement(Long likes, Long views, Long coments){
        if (views == 0){
            return new BigDecimal(0);
        }
        BigDecimal engagement= new BigDecimal(likes+coments).divide(new BigDecimal(views),2, RoundingMode.HALF_UP).multiply(new BigDecimal((100)));
        
        return engagement;
    }

    public SubmissionPaymentResponse getPayment(Long id) throws Exception{
        Submission submission = submisionRepository.findById(id).orElseThrow(() -> new RuntimeException("Submission no encontrada con id " + id));
        
        if (submission.getSubmissionStatus().equals(SubmissionStatus.PENDING) || submission.getSubmissionStatus().equals(SubmissionStatus.REJECTED)){
            throw new RuntimeException("Submisssion no aprobada");
        }

        if (submission.getSubmissionPayment() != null) {
            return new SubmissionPaymentResponse(
                submission.getSubmissionPayment().getViews(),
                submission.getSubmissionPayment().getEngagement(),
                submission.getSubmissionPayment().getPaymentStatus(),
                submission.getSubmissionPayment().getPayment(),
                submission.getSubmissionPayment().getPaymentReceived(),
                submission.getCampaign().getCurrency()
            );
        }

        VideoStatsResponse videoStatsResponse= scrapperService.updateScrap(submission.getSavedVideoUrl());

        if (videoStatsResponse == null){
            throw new RuntimeException("No se pudo obtener las estadisticas del video");
        }

        Campaign campaign = submission.getCampaign();

        SubmissionPayment submissionPayment = new SubmissionPayment();
        Wallet wallet = submission.getSocialMedia().getUser().getWallet();

        BigDecimal engagement= getEngagement(
            videoStatsResponse.likes(),
            videoStatsResponse.views(), 
            videoStatsResponse.comments());

        submissionPayment.setSubmission(submission);
        submissionPayment.setShareCount(videoStatsResponse.shares());
        submissionPayment.setComments(videoStatsResponse.comments());
        submissionPayment.setLikes(videoStatsResponse.likes());
        submissionPayment.setViews(videoStatsResponse.views());
        submissionPayment.setEngagement(engagement);

        
        if (engagement.compareTo(BigDecimal.valueOf(2)) > 0){
            
            submissionPayment.setPaymentStatus(PaymentStatus.APPROVED);
            BigDecimal payment=campaign.getCpm().multiply(new BigDecimal(submissionPayment.getViews()).divide(new BigDecimal(1000)));
            BigDecimal finalPayment = campaignService.getMaxPaymentAndBudget(campaign, payment);
            submissionPayment.setPayment(finalPayment);
            submissionPayment.setPaymentReceived(finalPayment.multiply(new BigDecimal(0.7)));
            // se guarda el presupuesto consumido
            campaign.setConsumedBudget(finalPayment.add(campaign.getConsumedBudget()));
            submission.setSubmissionStatus(SubmissionStatus.PAYED);

            if (submission.getCampaign().getCurrency().equals(Currency.PEN))
            {
                BigDecimal valueBefore= wallet.getPEN();
                wallet.setPEN(valueBefore.add(submissionPayment.getPaymentReceived()));

            } else {
                BigDecimal valueBefore=  wallet.getUSD();
                wallet.setUSD(valueBefore.add(submissionPayment.getPaymentReceived()));
            }
            
            
        } else {
            submission.setSubmissionStatus(SubmissionStatus.ERROR);
            submissionPayment.setPaymentStatus(PaymentStatus.REJECTED);
            submissionPayment.setPayment(BigDecimal.ZERO);
            submissionPayment.setPaymentReceived(BigDecimal.ZERO);
        }
        
        submisionRepository.save(submission);
        submissionPaymentRepository.save(submissionPayment);
        walletRepository.save(wallet);
        campaignRepository.save(campaign);
        return new SubmissionPaymentResponse(
            submissionPayment.getViews(),
            submissionPayment.getEngagement(),
            submissionPayment.getPaymentStatus(),
            submissionPayment.getPayment(),
            submissionPayment.getPaymentReceived(),
            submission.getCampaign().getCurrency()
        );
    }

    public MetricsCreatorResponse getMetricsCreator(Long userId) throws Exception{
        MetricsSimple metricsSimple = getAllSubmissionsCBC(userId,"creator");
        if (metricsSimple.quantity()==0){
            return new MetricsCreatorResponse(
                0L,
                0,
                0L,
                0L,
                0L,
                new ArrayList<>(),
                walletRepository.findByUserId(userId).get().getUSD(),
                walletRepository.findByUserId(userId).get().getPEN(),
                BigDecimal.ZERO,
                new ArrayList<>()
            );
        }
            
        List<IncomesResponse> incomes = new ArrayList<>();
        BigDecimal totalBalance = new BigDecimal(0);

        for (ShowFullSubmission submission : metricsSimple.submissions()) {
            if (submission.submissionStatus().equals(SubmissionStatus.PAYED)){
                incomes.add(new IncomesResponse(
                    submission.id(),
                    submission.income().image(),
                    submission.income().title(),
                    submission.income().money(),
                    submission.income().currency(),
                    submission.submittedAt().toLocalDate())
                );
                totalBalance= totalBalance.add(submission.income().money());
            }
        }


        return new MetricsCreatorResponse(
            metricsSimple.views(),
            metricsSimple.quantity(),
            metricsSimple.viewsIg(),
            metricsSimple.viewsFb(),
            metricsSimple.viewsTk(),
            metricsSimple.submissions(),
            walletRepository.findByUserId(userId).get().getUSD(),
            walletRepository.findByUserId(userId).get().getPEN(),
            totalBalance,
            incomes
        );
        
        
    }



}
