package com.inklop.inklop.controllers.user.response;

import com.inklop.inklop.entities.valueObject.user.UserRole;

import java.time.LocalDate;
import java.util.List;

public record LoginResponse(
        Long id,
        String realName,
        String avatarUrl,
        LocalDate birthDate,
        String email,
        String country,
        String city,
        UserRole role,
        WalletResponse wallet,
        List<SocialMediaResponse> socialMedias
) {
}
