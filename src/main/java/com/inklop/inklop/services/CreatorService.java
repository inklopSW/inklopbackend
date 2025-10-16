package com.inklop.inklop.services;

import com.inklop.inklop.controllers.user.request.CreatorRequest;
import com.inklop.inklop.controllers.user.response.LoginResponse;
import com.inklop.inklop.controllers.user.response.SocialMediaResponse;
import com.inklop.inklop.entities.Creator;
import com.inklop.inklop.entities.CreatorCategories;
import com.inklop.inklop.entities.User;
import com.inklop.inklop.entities.Wallet;
import com.inklop.inklop.entities.valueObject.user.UserRole;
import com.inklop.inklop.mappers.UserMapper;
import com.inklop.inklop.repositories.CreatorCategoriesRepository;
import com.inklop.inklop.repositories.CreatorRepository;
import com.inklop.inklop.repositories.UserRepository;
import com.inklop.inklop.repositories.WalletRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatorService {
    private final CreatorRepository creatorRepository;
    private final CreatorCategoriesRepository creatorCategoriesRepository;
    private final UserRepository userRepository;
    private final SocialMediaService socialMediaService;
    private final UserMapper userMapper;
    private final WalletRepository walletRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse createFullCreator(CreatorRequest request) {
        User user = userMapper.toEntity(request.user(), passwordEncoder.encode(request.user().password()), UserRole.CREATOR);
        user = userRepository.save(user); //Guardo al usuario

        Creator creator = new Creator();
        creator.setUser(user);
        creator.setDescription(request.description());
        creator.setAvatar(request.avatar());
        creator.setUsername(request.username());
        creator.setCreatorType(request.creatorType());


        Creator savedCreator = creatorRepository.save(creator);

        List<CreatorCategories> creatorCategories = request.categories().stream()
            .map(category -> {
                CreatorCategories cc = new CreatorCategories();
                cc.setCreator(savedCreator);
                cc.setCategory(category);
                return cc;
            })
            .toList();

        creatorCategoriesRepository.saveAll(creatorCategories);

        userRepository.flush(); // Asegura que el usuario se guarde antes de continuar

        List<SocialMediaResponse> socialMedias = socialMediaService.addSocialMedias(request.socialMedias(), user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);

        return userMapper.toLoginResponse(user, wallet, socialMedias);
    }

    
}
