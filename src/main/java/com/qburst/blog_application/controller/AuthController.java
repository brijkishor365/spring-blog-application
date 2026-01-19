package com.qburst.blog_application.controller;

import com.qburst.blog_application.dto.request.auth.LoginRequest;
import com.qburst.blog_application.dto.request.auth.ResetPasswordRequest;
import com.qburst.blog_application.dto.response.user.UserAddResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.qburst.blog_application.dto.response.auth.AuthResponse;
import com.qburst.blog_application.dto.request.user.UserAddRequest;
import com.qburst.blog_application.service.user.Impl.UserService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserAddResponse> register(@Valid @RequestBody UserAddRequest request) throws Exception {
        return new ResponseEntity<>(userService.registerUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }

    // @PostMapping("/refresh")
    // public ResponseEntity<AuthResponse> refreshToken(@RequestBody
    // TokenRefreshRequest request) {
    // return ResponseEntity.ok(authService.refreshToken(request));
    // }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization") String authorization) {
        // Controller just passes the raw data and returns the correct status
        userService.logout(authorization);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.ok("If an account exists, an OTP has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.email(), request.otp(), request.newPassword());
        return ResponseEntity.ok("Password reset successful.");
    }
}
