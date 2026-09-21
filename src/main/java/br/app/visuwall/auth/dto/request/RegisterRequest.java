package br.app.visuwall.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
        String password,

        @NotBlank(message = "Nome de usuário deve ter entre 3 caracteres e 80 caracteres")
        @Size(min = 3, max = 80)
        String username
) {}
