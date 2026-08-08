package com.babacar.secureauthservice.adapter.in.web;

import com.babacar.secureauthservice.domain.port.in.LoginUseCase;
import com.babacar.secureauthservice.domain.port.in.RefreshTokenUseCase;
import com.babacar.secureauthservice.domain.port.in.RegisterUserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @Test
    @DisplayName("409 : email déjà utilisé")
    @WithMockUser
    void should_return_409_when_email_already_registered() throws Exception {
        doThrow(new IllegalArgumentException("Email already registered: test@example.com"))
                .when(registerUserUseCase).register(any(), any());

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "test@example.com",
                      "password": "MonMotDePasse123"
                    }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflit"));
    }

    @Test
    @DisplayName("400 : validation échouée")
    @WithMockUser
    void should_return_400_on_validation_error() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "pasunmail",
                      "password": "abc"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("500 : erreur inattendue")
    @WithMockUser
    void should_return_500_on_unexpected_error() throws Exception {
        doThrow(new RuntimeException("Unexpected error"))
                .when(registerUserUseCase).register(any(), any());

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "test@example.com",
                      "password": "MonMotDePasse123"
                    }
                    """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}