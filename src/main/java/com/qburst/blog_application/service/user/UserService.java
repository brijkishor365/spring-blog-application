package com.qburst.blog_application.service.user;

import com.qburst.blog_application.dto.response.user.UserAddResponse;
import com.qburst.blog_application.dto.response.user.UserResponse;

import com.qburst.blog_application.dto.request.user.UserAddRequest;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserService {

    UserAddResponse registerUser(UserAddRequest user) throws Exception;

    void logout(String authToken);

    UserResponse getUserProfile(String username);

    UserResponse getUser(Long userId);

    void deleteUser(@PathVariable Long userId);
}
