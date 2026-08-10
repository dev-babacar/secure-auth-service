package com.babacar.secureauthservice.adapter.in.web;

import com.babacar.secureauthservice.domain.model.LoginResult;
import com.babacar.secureauthservice.domain.port.in.LoginUseCase;
import com.babacar.secureauthservice.domain.port.in.RefreshTokenUseCase;
import com.babacar.secureauthservice.domain.port.in.RegisterUserUseCase;
import com.babacar.secureauthservice.domain.port.out.TokenBlacklist;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenBlacklist tokenBlacklist;

    @Test
    @DisplayName("POST /register : retourne 201 si données valides")
    @WithMockUser
    void should_return_201_on_valid_register() throws Exception {
        doNothing().when(registerUserUseCase).register(any(), any());

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "test@example.com",
                      "password": "MonMotDePasse123"
                    }
                    """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /register : retourne 400 si email invalide")
    @WithMockUser
    void should_return_400_on_invalid_email() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "pasunmail",
                      "password": "MonMotDePasse123"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register : retourne 400 si password trop court")
    @WithMockUser
    void should_return_400_on_short_password() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "test@example.com",
                      "password": "abc"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /login : retourne 200 avec tokens si credentials valides")
    @WithMockUser
    void should_return_200_with_tokens_on_valid_login() throws Exception {
        when(loginUseCase.login(any(), any()))
                .thenReturn(new LoginResult("access-token", "refresh-token"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "test@example.com",
                      "password": "MonMotDePasse123"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    @DisplayName("POST /refresh : retourne 200 avec nouveaux tokens")
    @WithMockUser
    void should_return_200_on_valid_refresh() throws Exception {
        when(refreshTokenUseCase.refresh(any()))
                .thenReturn(new LoginResult("new-access", "new-refresh"));

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "refreshToken": "some-refresh-token"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"));
    }
}