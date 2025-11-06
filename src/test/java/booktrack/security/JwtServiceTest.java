package booktrack.security;

import booktrack.user.Role;
import booktrack.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

@ExtendWith(SpringExtension.class)
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private JwtService jwtService = new JwtService();

    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        String base64Key = Base64.getEncoder()
                .encodeToString("my-super-secure-jwt-secret-key-9999999999".getBytes());
        ReflectionTestUtils.setField(jwtService, "secretKey", base64Key);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

        testUser = User.builder()
                .email("test@test.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .build();
    }


    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        // When
        String token = jwtService.generateToken(testUser);

        // Then
        Assertions.assertAll(
                () -> assertThat(token).isNotNull(),
                () -> assertThat(token).isNotEmpty()

        );
    }

    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertThat(username).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Should validate token successfully")
    void shouldValidateTokenSuccessfully() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        boolean isValid = jwtService.isTokenValid(token, testUser);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate token for different user")
    void shouldInvalidateTokenForDifferentUser() {
        // Given
        String token = jwtService.generateToken(testUser);

        UserDetails differentUser = User.builder()
                .email("different@test.com")
                .password("password")
                .firstName("Different")
                .lastName("User")
                .role(Role.USER)
                .build();

        // When
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Then
        assertThat(isValid).isFalse();
    }
}
