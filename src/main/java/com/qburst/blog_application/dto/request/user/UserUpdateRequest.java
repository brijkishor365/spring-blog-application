package com.qburst.blog_application.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank String email,
        @NotBlank String password
) {
}
