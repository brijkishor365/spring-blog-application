package com.qburst.blog_application.controller;

import com.qburst.blog_application.dto.request.user.UserRequest;
import com.qburst.blog_application.dto.response.user.UserListResponse;
import com.qburst.blog_application.dto.response.user.UserResponse;
import com.qburst.blog_application.service.user.Impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor // Automatically generates the constructor for all 'final' fields
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserListResponse>> getUsers(
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.info("CURRENT USER AUTHORITIES: {}", auth.getAuthorities());

        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {

        UserResponse userDetails = userService.getUser(userId);

        return new ResponseEntity<>(userDetails,HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getAdminProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse userProfile = userService.getUserProfile(userDetails.getUsername());

        return ResponseEntity.ok(userProfile);
    }

    @PutMapping(value = "/me", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponse> updateAdminProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserRequest request) {

        UserResponse updatedUser = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long userId) {
        log.info("Request to delete user with ID: {}", userId);
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
