package com.inklop.inklop.services;

import com.inklop.inklop.controllers.user.request.BusinessRequest;
import com.inklop.inklop.controllers.user.response.LoginResponse;
import com.inklop.inklop.controllers.user.response.SocialMediaResponse;
import com.inklop.inklop.entities.User;
import com.inklop.inklop.entities.Wallet;
import com.inklop.inklop.entities.valueObject.user.UserRole;
import com.inklop.inklop.mappers.UserMapper;
import com.inklop.inklop.entities.Business;
import com.inklop.inklop.repositories.BusinessRepository;
import com.inklop.inklop.repositories.UserRepository;
import com.inklop.inklop.repositories.WalletRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BusinessService {

    @Autowired
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SocialMediaService socialMediaService;
    private final WalletRepository walletRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    
    @Transactional
    public LoginResponse createFullBusiness(BusinessRequest request) {
        User user = userMapper.toEntity(request.user(), passwordEncoder.encode(request.user().password()), UserRole.BUSINESS);
        user = userRepository.save(user);

        Business business = new Business();
        business.setUser(user);
        business.setBusinessName(request.businessName());
        business.setDescription(request.description());
        business.setRuc(request.ruc());
        business.setAvatarBusiness(request.businessImage());
        business.setBusinessType(request.businessType());
        business.setSector(request.sector());
        businessRepository.save(business);

        userRepository.flush(); // Asegura que el usuario se guarde antes de continuar

        List<SocialMediaResponse> socialMedias = socialMediaService.addSocialMedias(request.socialMedias(),user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);
        
        return userMapper.toLoginResponse(user, wallet, socialMedias, request.businessName());
    }   
        
    
}
