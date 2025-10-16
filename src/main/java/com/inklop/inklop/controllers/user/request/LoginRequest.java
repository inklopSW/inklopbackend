package com.inklop.inklop.controllers.user.request;

public record LoginRequest(
        String email,
        String password
) {
}
