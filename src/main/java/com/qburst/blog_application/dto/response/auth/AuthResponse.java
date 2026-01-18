package com.qburst.blog_application.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // apply to all fields at once, others are  - UpperCamelCaseStrategy: firstName -> FirstName, KebabCaseStrategy: firstName -> first-name, LowerDotCaseStrategy: firstName -> first.name or set in application.properties -> spring.jackson.property-naming-strategy=SNAKE_CASE
public class AuthResponse {
    @JsonProperty(value = "access_token", required = true)
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
