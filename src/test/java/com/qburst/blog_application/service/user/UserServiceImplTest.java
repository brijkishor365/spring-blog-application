package com.qburst.blog_application.service.user;

import com.qburst.blog_application.dto.request.auth.LoginRequest;
import com.qburst.blog_application.dto.request.user.UserRequest;
import com.qburst.blog_application.dto.response.auth.AuthResponse;
import com.qburst.blog_application.dto.response.user.UserListResponse;
import com.qburst.blog_application.dto.response.user.UserResponse;
import com.qburst.blog_application.entity.UserEntity;
import com.qburst.blog_application.exception.auth.InvalidUserNameOrPasswordException;
import com.qburst.blog_application.exception.user.UserNameAlreadyExistsException;
import com.qburst.blog_application.mapper.UserMapper;
import com.qburst.blog_application.repository.BlacklistedTokenRepository;
import com.qburst.blog_application.repository.UserRepository;
import com.qburst.blog_application.service.email.EmailService;
import com.qburst.blog_application.service.jwt.JwtService;
import com.qburst.blog_application.service.user.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity userEntity;
    private UserResponse userResponse;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        // Initialize UserRequest (Record)
        userRequest = new UserRequest(
                1L,
                "brij_user",
                "brijpass",
                "ROLE_USER",
                "Brij",
                "Kishor",
                "brij@gmail.com"
        );

        // Initialize UserEntity
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("brij_user");
        userEntity.setEmail("brij@gmail.com");
        userEntity.setFirstname("Brij");
        userEntity.setLastname("Kishor");

        // Initialize UserResponse (Lombok @Data)
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("brij_user");
        userResponse.setEmail("brij@gmail.com");
    }

    @Test
    @DisplayName("Should register user successfully when username is unique")
    void testRegisterUser_Success() throws Exception {
        // Arrange
        // Mock the check: list.isEmpty() should be true
        when(userRepository.findUserByUsername(userRequest.username())).thenReturn(Collections.emptyList());
        when(userMapper.toEntity(any(UserRequest.class))).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);

        // Act
        UserResponse result = userService.registerUser(userRequest);

        // Assert
        assertNotNull(result);
        assertEquals("brij@gmail.com", result.getEmail());
        assertEquals("brij_user", result.getUsername());

        // Verify interactions
        verify(userRepository, times(1)).findUserByUsername(userRequest.username());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testRegisterUser_UsernameExists() {
        // Arrange
        when(userRepository.findUserByUsername(userRequest.username()))
                .thenReturn(List.of(userEntity)); // List is NOT empty

        // Act & Assert
        assertThrows(UserNameAlreadyExistsException.class, () -> {
            userService.registerUser(userRequest);
        });

        // Verify save was never called
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // --- AUTHENTICATION & LOGOUT ---

    @Test
    void testAuthenticate_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("brij@gmail.com", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateAccessToken(userDetails)).thenReturn("mocked-jwt-token");

        // Act
        AuthResponse response = userService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("wrong@gmail.com", "wrongpass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(InvalidUserNameOrPasswordException.class, () ->
                userService.authenticate(loginRequest));
    }

    @Test
    void testLogout_Success() {
        // Arrange
        String authHeader = "Bearer valid-token";
        Instant expiry = Instant.now().plusSeconds(3600);
        when(jwtService.extractExpiry("valid-token")).thenReturn(expiry);

        // Act & Assert
        assertDoesNotThrow(() -> userService.logout(authHeader));
        verify(blacklistedTokenRepository, times(1)).save(any());
    }

    // --- PROFILE RETRIEVAL ---

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        String username = "brij_user";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(modelMapper.map(userEntity, UserResponse.class)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.getUserProfile(username);

        // Assert
        assertNotNull(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testGetUser_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(modelMapper.map(userEntity, UserResponse.class)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.getUser(userId);

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(userId);
    }

    // --- UPDATE & DELETE ---

    @Test
    void testUpdateProfile_Success() {
        // Arrange
        String username = "brij_user";
        UserRequest updateRequest = new UserRequest(1L, null, "newpass", null, "NewFirst", "NewLast", "new@email.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(userResponse);

        // Act
        UserResponse result = userService.updateProfile(username, updateRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).delete(userEntity);
    }

    // --- PAGINATION ---

    @Test
    void testGetUsers_Pagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<UserEntity> userPage = new PageImpl<>(List.of(userEntity));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(modelMapper.map(any(), eq(UserListResponse.class))).thenReturn(new UserListResponse());

        // Act
        Page<UserListResponse> result = userService.getUsers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository).findAll(pageable);
    }
}