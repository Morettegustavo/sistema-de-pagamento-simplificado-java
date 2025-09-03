# Sistema de Pagamentos Simplificado  

## Objetivo  

Este projeto é um sistema de pagamentos simplificado inspirado em plataformas digitais. Ele permite que usuários realizem **depósitos** e **transferências** de dinheiro entre si.  

O sistema possui dois tipos de usuários:  
- **Usuários comuns**: podem enviar e receber transferências.  
- **Lojistas**: apenas recebem transferências, não podem enviar dinheiro.  

## Regras de Negócio  

- Cadastro exige `Nome Completo`, `CPF`, `E-mail` e `Senha`.  
  - CPF/CNPJ e e-mails devem ser **únicos** no sistema.  
- Usuários podem transferir dinheiro para outros usuários ou para lojistas.  
- Lojistas **somente recebem** transferências.  
- O sistema valida se o usuário possui **saldo suficiente** antes da transferência.  
- Antes de finalizar uma transferência, o sistema consulta um serviço autorizador externo (mock).  
- O processo de transferência é realizado como **transação**: em caso de falha, deve ser revertido.  
- Após o recebimento, o usuário/lojista deve receber **notificação** via serviço externo (mock).  
- O serviço segue o padrão **RESTful**.  

## Endpoint de Transferência  

```http
POST /transfer
Content-Type: application/json

{
  "amount": 100.0,
  "senderId": 4,
  "receiverId": 15
}
