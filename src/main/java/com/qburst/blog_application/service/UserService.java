package com.qburst.blog_application.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import com.qburst.blog_application.Document.BlacklistedToken;
import com.qburst.blog_application.dto.request.auth.LoginRequest;
import com.qburst.blog_application.dto.response.user.UserAddResponse;
import com.qburst.blog_application.dto.response.user.UserListResponse;
import com.qburst.blog_application.dto.response.user.UserResponse;
import com.qburst.blog_application.dto.request.user.UserUpdateRequest;
import com.qburst.blog_application.exception.*;
import com.qburst.blog_application.mapper.UserMapper;
import com.qburst.blog_application.repository.BlacklistedTokenRepository;
import com.qburst.blog_application.service.email.EmailService;
import com.qburst.blog_application.service.jwt.JwtService;
import com.qburst.blog_application.service.user.UserServiceInterface;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.qburst.blog_application.dto.response.auth.AuthResponse;
import com.qburst.blog_application.dto.request.user.UserAddRequest;
import com.qburst.blog_application.entity.UserEntity;
import com.qburst.blog_application.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    private final EmailService emailService;

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper, EmailService emailService, BlacklistedTokenRepository blacklistedTokenRepository) {
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    @Transactional
    public UserAddResponse registerUser(UserAddRequest userAddRequest) throws Exception {

        List<UserEntity> list = userRepository.findUserByUsername(userAddRequest.username());

        if (!list.isEmpty()) {
            throw new UserNameAlreadyExistsException("Username '" + userAddRequest.username() + "' is already taken.");
        }

        UserEntity userEntity = userMapper.toEntity(userAddRequest);

        userEntity.setPassword(passwordEncoder.encode(userAddRequest.password()));
        userEntity.setRoles("ROLE_USER");

        UserEntity savedUser = userRepository.save(userEntity);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public void logout(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String token = authorization.substring(7);

        try {
            Instant expiry = jwtService.extractExpiry(token);
            blacklistedTokenRepository.save(new BlacklistedToken(token, expiry));
        } catch (Exception e) {
            throw new TokenProcessingException("Failed to blacklist token");
        }
    }

    @Override
    public UserResponse getUserProfile(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return this.modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        return this.modelMapper.map(user, UserResponse.class);
    }

    public AuthResponse authenticate(LoginRequest request) {
        try {
            // This triggers UserDetailsService and checks the password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateAccessToken(userDetails);

//            if (userDetails instanceof UserPrincipal principal) {
//                log.info("Login successful for User ID: {}", principal.getUserEntity().getId());
//            }
            log.info("User login successful: {}", userDetails.getUsername());

            return new AuthResponse(token);

        } catch (BadCredentialsException | InternalAuthenticationServiceException ex) {
            // Map Spring's internal exceptions to your custom business exception
            throw new InvalidUserNameOrPasswordException("Invalid username or password");
        }
    }

    @Transactional
    public UserResponse updateProfile(String username, UserUpdateRequest userUpdateRequest) {
        // Find the existing user or throw exception
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        // Map only allowed fields (Manual or using ModelMapper)
        // Avoid updating sensitive fields like roles or username here
        if (userUpdateRequest.firstname() != null) userEntity.setFirstname(userUpdateRequest.firstname());
        if (userUpdateRequest.lastname() != null) userEntity.setLastname(userUpdateRequest.lastname());
        if (userUpdateRequest.email() != null) userEntity.setEmail(userUpdateRequest.email());

        // Handle Password separately
        if (userUpdateRequest.password() != null && !userUpdateRequest.password().isBlank()) {
            userEntity.setPassword(passwordEncoder.encode(userUpdateRequest.password()));
        }

        // Save and return the DTO
        UserEntity savedUser = userRepository.save(userEntity);

        return modelMapper.map(savedUser, UserResponse.class);
    }

    public Page<UserListResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> modelMapper.map(user, UserListResponse.class));
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check Expiry
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired");
        }

        // Validate OTP
        if (!passwordEncoder.matches(otp, user.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // Success - Update Password and Clear OTP
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Generate 6-digit OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setOtp(passwordEncoder.encode(otp)); // Securely hash the OTP
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10)); // Valid for 10 mins

        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }
}
