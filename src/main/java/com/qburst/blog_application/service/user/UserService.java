package com.qburst.blog_application.service.user;

import com.qburst.blog_application.dto.response.user.UserResponse;

import com.qburst.blog_application.dto.request.user.UserRequest;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserService {

    UserResponse registerUser(UserRequest user) throws Exception;

    void logout(String authToken);

    UserResponse getUserProfile(String username);

    UserResponse getUser(Long userId);

    void deleteUser(@PathVariable Long userId);
}
