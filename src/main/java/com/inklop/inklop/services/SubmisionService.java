package com.inklop.inklop.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.inklop.inklop.controllers.submission.request.SimpleSubmissionRequest;
import com.inklop.inklop.controllers.submission.response.SubmissionPaymentResponse;
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
import com.inklop.inklop.entities.valueObject.user.CreatorType;
import com.inklop.inklop.entities.valueObject.user.Platform;
import com.inklop.inklop.mappers.ScrapperMapper;
import com.inklop.inklop.repositories.SocialMediaRepository;
import com.inklop.inklop.repositories.SubmissionRepository;
import com.inklop.inklop.repositories.UserRepository;
import com.inklop.inklop.repositories.SubmissionPaymentRepository;
import com.inklop.inklop.repositories.WalletRepository;
import com.inklop.inklop.repositories.Campaign.CampaignRepository;
import com.inklop.inklop.services.scrapper.ScrapperService;
import com.inklop.inklop.services.scrapper.dto.PostResponse;
import com.inklop.inklop.services.scrapper.dto.VideoStatsResponse;
import com.inklop.inklop.services.videoAnalyze.AsyncVideoEvaluator;
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
    private final SocialMediaRepository socialMediaRepository;
    private final ScrapperService scrapperService;
    private final ScrapperMapper scrapperMapper;
    private final AsyncVideoEvaluator asyncVideoEvaluator;

    public ShowFullSubmission saveSubmission(SimpleSubmissionRequest submissionRequest){
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

        if (!campaign.getHasFacebook()){
            if (socialMedia.getPlatform().equals(Platform.FACEBOOK)){
                throw new RuntimeException("Campaign does not accept Facebook submissions");
            }
        }

        if (!campaign.getHasInstagram()){
            if (socialMedia.getPlatform().equals(Platform.INSTAGRAM)){
                throw new RuntimeException("Campaign does not accept Instagram submissions");
            }
        }
        
        if (!campaign.getHasTiktok()){
            if (socialMedia.getPlatform().equals(Platform.TIKTOK)){
                throw new RuntimeException("Campaign does not accept TikTok submissions");
            }
        }       

        IncomeDto incomeDto= new IncomeDto(BigDecimal.ZERO, campaign.getCurrency(), campaign.getName(), campaign.getLogo());
        PostResponse videoInfo = scrapperService.postVideoToExternalApi(submissionRequest.videoUrl(), socialMedia.getPlatform(), campaign.getEndDate());
        
        Submission submission = new Submission();
        submission.setCampaign(campaign);
        submission.setSocialMedia(socialMedia);
        submission.setVideoUrl(videoInfo.video_url());

        // Verificar si el video ya fue enviado anteriormente
        if (submisionRepository.existsBySavedVideoUrl(videoInfo.video_url())) {
            submission.setDescription("Video has already been submitted");
            submission.setSubmissionStatus(SubmissionStatus.REJECTED);
            submission.setPercentage(0);
            submission = submisionRepository.save(submission);
            return new ShowFullSubmission(
                submission.getId(),
                submission.getSubmissionStatus(),
                PaymentStatus.REJECTED,
                submission.getSubmittedAt(),
                submission.getDescription(),
                incomeDto,
                scrapperMapper.toVideoStatsResponse(videoInfo)
            );

        }
        
        if ( !videoInfo.owner_id().equals(socialMedia.getOwnerId())){
            submission.setDescription("Video does not belong to the user");
            submission.setSubmissionStatus(SubmissionStatus.REJECTED);
            submission.setPercentage(0);
            submission = submisionRepository.save(submission);
            return new ShowFullSubmission(
                submission.getId(),
                submission.getSubmissionStatus(),
                PaymentStatus.REJECTED,
                submission.getSubmittedAt(),
                submission.getDescription(),
                incomeDto,
                scrapperMapper.toVideoStatsResponse(videoInfo)
            );
        }

        Instant utcIns = Instant.parse(videoInfo.timestamp());
        LocalDateTime videoTimestamp = LocalDateTime.ofInstant(utcIns, ZoneId.of("America/Lima"));
        
        
        // video debe ser subido luego de la fecha de inicio de la campaña
        /* 
        if (videoTimestamp.toLocalDate().isBefore(campaign.getStartDate())) {
            submission.setDescription("Video submitted before campaign start date");
            submission.setSubmissionStatus(SubmissionStatus.REJECTED);
            submission.setPercentage(0);
            submission = submisionRepository.save(submission);
            return new ShowFullSubmission(
                submission.getId(),
                submission.getSubmissionStatus(),
                PaymentStatus.REJECTED,
                submission.getSubmittedAt(),
                submission.getDescription(),
                incomeDto,
                scrapperMapper.toVideoStatsResponse(videoInfo)
            );
        }
        */
        submission.setDescription("Submission pending review");
        submission.setSubmissionStatus(SubmissionStatus.PENDING);
        submission=submisionRepository.save(submission);

        if (campaign.getType().equals(CreatorType.UGC)){
            asyncVideoEvaluator.evaluateSubmissionAsync(submission.getId());
        } else {
            
        }

        return new ShowFullSubmission(
                submission.getId(),
                submission.getSubmissionStatus(),
                PaymentStatus.PENDING,
                submission.getSubmittedAt(),               
                submission.getDescription(),
                incomeDto,
                scrapperMapper.toVideoStatsResponse(videoInfo)
            );

    }

    public ShowFullSubmission sendToApproveAgain(Long id){
        Submission submission = submisionRepository.findById(id).get();

        // only submissions in PENDING status can be sent for re-evaluation
        if (!submission.getSubmissionStatus().equals(SubmissionStatus.PENDING)){
            throw new RuntimeException("Submission is not in pending status");
        }

        if (submission.getCampaign().getType().equals(CreatorType.UGC)){
            asyncVideoEvaluator.evaluateSubmissionAsync(submission.getId());
        } else {
            
        }
        IncomeDto incomeDto= new IncomeDto(BigDecimal.ZERO, submission.getCampaign().getCurrency(), submission.getCampaign().getName(), submission.getCampaign().getLogo());
        return new ShowFullSubmission(
            submission.getId(),
            submission.getSubmissionStatus(),
            PaymentStatus.PENDING,
            submission.getSubmittedAt(),
            submission.getDescription(),
            incomeDto,
            null
        );

    
    }

    public MetricsSimple getMetricsAndMappingSubmissions(List<Submission> submissions){
        Long views=0L;
        Long likes=0L;
        Long comments=0L;
        Long shareCount=0L;
        Integer quantity=0;
        Long viewsFb=0L;
        Long viewsTk=0L;
        Long viewsIg=0L;
        List<ShowFullSubmission> showFullSubmissions = new ArrayList<>();
        List<String> urls= submissions.stream().map(Submission::getVideoUrl).toList();
        log.warn("URLs to fetch stats: {}", urls);

        Map<String, VideoStatsResponse> postsByUrl = scrapperService.getAllPosts(urls);
        log.warn("Fetched video stats for URLs: {}", postsByUrl.keySet());


        for (Submission submission : submissions) {
            BigDecimal payment= BigDecimal.ZERO;
            VideoStatsResponse postResponse= postsByUrl.get(submission.getVideoUrl());
            log.warn("Processing submission ID {} with video URL {}", submission.getId(), submission.getVideoUrl());

            if (postResponse == null) {
                log.warn("getAllSubmissionsCBC -> No stats for url={} skipping or using defaults", submission.getVideoUrl());
                continue; // o usar valores por defecto
            }
            // calcular payment de forma segura
            BigDecimal viewsBd = BigDecimal.valueOf(postResponse.views());
            payment=submission.getCampaign().getCpm().multiply(viewsBd.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP));
            payment=campaignService.getMaxPaymentAndBudget(submission.getCampaign(), payment);
            
            if (submission.getSubmissionStatus().equals(SubmissionStatus.REJECTED)){
                payment=new BigDecimal(0);
            }

            // paymentStatus base
            PaymentStatus paymentStatus= PaymentStatus.PENDING;
            if (submission.isRejected()){
                paymentStatus= PaymentStatus.REJECTED;
            }

            if (submission.getSubmissionPayment() != null) {
                payment=submission.getSubmissionPayment().getPayment();
                paymentStatus=submission.getSubmissionPayment().getPaymentStatus();
            }

            if (submission.isApproved() || submission.getSubmissionStatus().equals(SubmissionStatus.PAYED)){
                if (submission.getSocialMedia().getPlatform().equals(Platform.INSTAGRAM)){
                    viewsIg+=postResponse.views();
                } else if (submission.getSocialMedia().getPlatform().equals(Platform.FACEBOOK)){
                    viewsFb+=postResponse.views();
                } else if(submission.getSocialMedia().getPlatform().equals(Platform.TIKTOK)){
                    viewsTk+=postResponse.views();            
                }
                
                views+= postResponse.views();
                likes+= postResponse.likes();
                comments+= postResponse.comments();
                shareCount+= postResponse.shares();
                quantity+=1;
            }
            
            log.warn("Submission ID {}: views={}, likes={}, comments={}, shares={}, payment={}, paymentStatus={}",
                submission.getId(),
                postResponse.views(),
                postResponse.likes(),
                postResponse.comments(),
                postResponse.shares(),
                payment,
                paymentStatus);
            showFullSubmissions.add(
                new ShowFullSubmission(
                    submission.getId(),
                    submission.getSubmissionStatus(),
                    paymentStatus,
                    submission.getSubmittedAt(),
                    submission.getDescription(),
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

    public MetricsCampaignResponse getMetricsByCampaignId (Long campaignId){
        MetricsSimple metricsSimple = getMetricsAndMappingSubmissions(
            submisionRepository.findAllByCampaignId(campaignId)
        );
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

    public MetricsBusinessResponse getMetricsBussiness(Long userId){
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

        List<Submission> submissions = new ArrayList<>();
        BigDecimal totalBudget=new BigDecimal(0);
        for (Campaign campaign : campaigns) {
            totalBudget = totalBudget.add(campaign.getTotalBudget());
            submissions.addAll(campaign.getSubmissions());
        }

        MetricsSimple metricsSimple = getMetricsAndMappingSubmissions(submissions); // is better to use jpql with joins but idk uwu

        if (metricsSimple.submissions().isEmpty()){
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

    //to views for any problem maybe u know uwu
    private BigDecimal getEngagement(Long likes, Long views, Long coments){
        if (views == 0){
            return new BigDecimal(0);
        }
        return new BigDecimal(likes+coments)
            .divide(new BigDecimal(views),6, RoundingMode.HALF_UP)
            .multiply(new BigDecimal((100)))
            .setScale(2, RoundingMode.HALF_UP);
    }

    public SubmissionPaymentResponse getPayment(Long id){
        Submission submission = submisionRepository.findById(id).orElseThrow(() -> new RuntimeException("Submission no encontrada con id " + id));
        
        if (!submission.isApproved()){
            throw new RuntimeException("Submission no aprobada, no se puede calcular el pago");
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
        submissionPayment.setBookmarks(videoStatsResponse.bookmarks());
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
            submission.setDescription("Video rejected due to low engagement rate: " + engagement + "%");
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

    public MetricsCreatorResponse getMetricsCreator(Long userId){
        MetricsSimple metricsSimple = getMetricsAndMappingSubmissions(
            submisionRepository.findAllBySocialMediaUserId(userId
            ));
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Wallet not found for user id " + userId));
        if (metricsSimple.submissions().isEmpty()){
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
            wallet.getUSD(),
            wallet.getPEN(),
            totalBalance,
            incomes
        );
        
        
    }



}
