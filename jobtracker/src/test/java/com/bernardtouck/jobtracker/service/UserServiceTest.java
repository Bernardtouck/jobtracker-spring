package com.bernardtouck.jobtracker.service;

import com.bernardtouck.jobtracker.dto.AuthResponse;
import com.bernardtouck.jobtracker.dto.LoginRequest;
import com.bernardtouck.jobtracker.dto.RegisterRequest;
import com.bernardtouck.jobtracker.entity.User;
import com.bernardtouck.jobtracker.repository.UserRepository;
import com.bernardtouck.jobtracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // ─── Mocks — simuler les dépendances ─────────────────────
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    // ─── Inject — injecter les mocks dans le service ──────────
    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Arrange — données communes à tous les tests
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("123456");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("123456");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setPassword("hashed_password");
    }

    // ─── TEST 1 : Register — cas normal ───────────────────────
    @Test
    void register_ShouldReturnTokenAndUser_WhenEmailIsNew() {
        // Arrange
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken("test@test.com")).thenReturn("fake.jwt.token");

        // Act
        AuthResponse response = userService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.getToken());
        assertEquals("test@test.com", response.getUser().getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ─── TEST 2 : Register — email déjà utilisé ───────────────
    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(registerRequest));

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ─── TEST 3 : Login — cas normal ──────────────────────────
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken("test@test.com")).thenReturn("fake.jwt.token");

        // Act
        AuthResponse response = userService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.getToken());
        assertEquals("test@test.com", response.getUser().getEmail());
    }

    // ─── TEST 4 : GetProfile ──────────────────────────────────
    @Test
    void getProfile_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        // Act
        var result = userService.getProfile("test@test.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }
}