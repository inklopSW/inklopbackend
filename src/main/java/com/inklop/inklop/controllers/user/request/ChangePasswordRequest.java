package com.inklop.inklop.controllers.user.request;

public record ChangePasswordRequest(
    String oldPassword,
    String newPassword
) {    
}
