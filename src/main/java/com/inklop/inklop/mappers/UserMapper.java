package com.inklop.inklop.mappers;

import com.inklop.inklop.controllers.user.request.UserRegisterRequest;
import com.inklop.inklop.controllers.user.response.LoginResponse;
import com.inklop.inklop.controllers.user.response.SocialMediaResponse;
import com.inklop.inklop.entities.User;
import com.inklop.inklop.entities.Wallet;
import com.inklop.inklop.entities.valueObject.user.UserRole;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "realName", source = "request.real_name")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "avatarUrl", source = "request.avatarUrl")
    @Mapping(target = "birthDate", source = "request.birthDate")
    @Mapping(target = "typeDocument", source = "request.typeDocument")
    @Mapping(target = "document", source = "request.document")
    @Mapping(target = "country", source = "request.country")
    @Mapping(target = "city", source = "request.city")
    User toEntity(UserRegisterRequest request, String password, UserRole userRole);

    //next
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "realName", source = "user.realName")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    @Mapping(target = "birthDate", source = "user.birthDate")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "country", source = "user.country")
    @Mapping(target = "city", source = "user.city")
    @Mapping(target = "role", source = "user.userRole")
    @Mapping(target = "wallet.balancePEN", source = "wallet.PEN")
    @Mapping(target = "wallet.balanceUSD", source = "wallet.USD")
    @Mapping(target = "socialMedias", source = "socialMedias")
    LoginResponse toLoginResponse(User user, Wallet wallet, List<SocialMediaResponse> socialMedias, String username);
}
