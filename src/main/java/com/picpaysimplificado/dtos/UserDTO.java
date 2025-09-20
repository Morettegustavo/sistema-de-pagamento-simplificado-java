package com.picpaysimplificado.dtos;

import com.picpaysimplificado.domain.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

        @NotNull(message = "O tipo de usuário é obrigatório")
        @Schema(example = "COMMON")
        UserType type
) {
}
