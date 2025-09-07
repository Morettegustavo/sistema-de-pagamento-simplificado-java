package com.picpaysimplificado.exceptions;

public enum ErrorMessages {
    UNAUTHORIZED_TRANSACTION_NON_COMMON("Usuário do tipo Lojista não está autorizado a realizar transação"),
    INSUFFICIENT_BALANCE("Saldo insuficiente"),
    USER_NOT_FOUND("Usuário não encontrado"),
    UNAUTHORIZED_TRANSACTION("Transação não autorizada"),
    TRANSACTION_FAILED("Falha ao processar a transação");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
