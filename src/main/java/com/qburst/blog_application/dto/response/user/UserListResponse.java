package com.qburst.blog_application.dto.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "username", "email", "roles", "firstname"})
//@JsonInclude(JsonInclude.Include.NON_NULL) // include only non-null values in api response
//@JsonIgnoreProperties(ignoreUnknown = true) // ignore unknown properties
public class UserListResponse {
    private Long id;

    @JsonProperty(value = "username", required = true)
    private String username;

    @JsonProperty(value = "roles", required = true)
    private String roles;

//    @JsonProperty("active")
//    private boolean active;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty(value = "email", required = true)
    private String email;
}
