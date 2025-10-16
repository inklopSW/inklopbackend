package com.inklop.inklop.controllers.user.request;

import java.time.LocalDate;

import com.inklop.inklop.entities.valueObject.user.TypeDocument;

public record UserRegisterRequest(
    String real_name,
    String email,
    String password,
    TypeDocument typeDocument,
    String document,
    String avatarUrl,
    String country,
    String city,
    LocalDate birthDate
) {
}

