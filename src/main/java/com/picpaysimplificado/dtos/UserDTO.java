package com.picpaysimplificado.dtos;

import com.picpaysimplificado.domain.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

public record UserDTO(

        @NotBlank(message = "firstName é obrigátorio")
        @Schema(example = "João")
        String firstName,

        @NotBlank(message = "lastName é obrigatório")
        @Schema(example = "Silva")
        String lastName,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
        @Schema(example = "12345678909")
        String cpf,

        @NotNull(message = "balance é obrigatório")
        @DecimalMin(value = "0.0", inclusive = true, message = "O saldo não pode ser negativo")
        @Schema(example = "10.00")
        BigDecimal balance,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Schema(example = "joao@gmail.com")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        @Schema(example = "senh@123")
        String password,

        @NotNull(message = "O tipo de usuário é obrigatório")
        @Schema(example = "COMMON")
        UserType type
) {
}
