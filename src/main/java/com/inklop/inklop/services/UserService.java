package com.inklop.inklop.services;
import com.inklop.inklop.controllers.user.request.ChangePasswordRequest;
import com.inklop.inklop.controllers.user.request.LoginRequest;
import com.inklop.inklop.controllers.user.response.LoginResponse;
import com.inklop.inklop.controllers.user.response.SocialMediaResponse;
import com.inklop.inklop.entities.User;
import com.inklop.inklop.mappers.UserMapper;
import com.inklop.inklop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.inklop.inklop.controllers.user.response.WalletResponse;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository uR;
    private final SocialMediaService smS;
    private final UserMapper uM;
    private final FileService fS;


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public LoginResponse login(LoginRequest request) {
        User user = uR.findByEmail(request.email())
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            List<SocialMediaResponse> socialMedias = user.getSocialMedias().stream()
                    .map(smS::getSocialMediaResponse)
                    .toList();

            return uM.toLoginResponse(user, user.getWallet(),socialMedias);
        }

        throw new IllegalArgumentException("Credenciales inválidas");
    }

    public WalletResponse getWallet(Long idUser) {
        User user = uR.findById(idUser)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new WalletResponse(
                user.getWallet().getPEN(),
                user.getWallet().getUSD()
        );
    }


    public Boolean changePassword(Long id, ChangePasswordRequest request) {
        User user = uR.findById(id).get();
        if (passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.newPassword()));
            uR.save(user);
            return true;
        }
        return false;
    }

    public String changeImage(Long id, MultipartFile file) throws IOException {
        User user = uR.findById(id).get();
        String url = fS.uploadImgeString(file);
        user.setAvatarUrl(url);
        uR.save(user);
        return url;
    }
}
