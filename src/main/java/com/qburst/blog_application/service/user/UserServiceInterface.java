package com.qburst.blog_application.service.user;

import com.qburst.blog_application.dto.response.user.UserAddResponse;
import com.qburst.blog_application.dto.response.user.UserResponse;

import com.qburst.blog_application.dto.request.user.UserAddRequest;

public interface UserServiceInterface {

    public UserAddResponse registerUser(UserAddRequest user) throws Exception;

    public void logout(String authToken);

    public UserResponse getUserProfile(String username);

    UserResponse getUser(Long userId);
}
