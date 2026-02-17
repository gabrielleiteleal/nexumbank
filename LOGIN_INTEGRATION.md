# Sistema de Login - Integração Frontend e Backend

## 📋 O que foi implementado

Sistema completo de autenticação de usuários conectando o formulário de login (index.html) à API REST.

## 🆕 Arquivos Criados

### 1. **Backend - DTOs**

#### `UsuarioLoginRequestDTO.java`
```java
public record UsuarioLoginRequestDTO(String cpf_cnpj, String senha) {}
```
DTO para receber as credenciais de login do frontend.

#### `UsuarioLoginResponseDTO.java`
```java
public record UsuarioLoginResponseDTO(
    String id_usuario,
    String nome,
    String cpf_cnpj,
    String email,
    String tipo_usuario,
    Long id_cliente,
    Long id_conta
) {}
```
DTO para retornar os dados do usuário autenticado, incluindo IDs do cliente e conta.

### 2. **Backend - Repository**
Adicionado método em `IUsuario.java`:
```java
Optional<Usuario> findByCpfCnpj(String cpfCnpj);
```

### 3. **Backend - Service**
Adicionado método de login em `UsuarioService.java`:
```java
public UsuarioLoginResponseDTO login(UsuarioLoginRequestDTO loginRequest) {
    // Busca usuário por CPF/CNPJ
    // Valida senha com BCrypt
    // Retorna dados do usuário incluindo ID da conta
}
```

### 4. **Backend - Controller**
Adicionado endpoint em `UsuarioController.java`:
```java
@PostMapping("/login")
public ResponseEntity<UsuarioLoginResponseDTO> login(@RequestBody UsuarioLoginRequestDTO loginRequest)
```

### 5. **Frontend - JavaScript**
Criado `login.js` com:
- Máscaras de CPF/CNPJ
- Toggle de senha
- Validação de formulário
- Chamada à API de login
- Salvamento de dados no sessionStorage
- Redirecionamento para conta.html
- Modais de erro

### 6. **Frontend - HTML**
Atualizado `index.html`:
- Substituído JavaScript inline pelo arquivo externo
- Formulário conectado à API

## 🔐 Como funciona o fluxo de autenticação

```
[Usuário digita CPF e senha]
        ↓
[login.js valida campos]
        ↓
[POST /usuario/login]
        {
          "cpf_cnpj": "000.000.000-88",
          "senha": "12345678"
        }
        ↓
[UsuarioController.login()]
        ↓
[UsuarioService.login()]
        ↓
[Busca usuário por CPF/CNPJ]
        ↓
[Valida senha com BCrypt]
        ↓
[Se CLIENTE: busca ID da conta]
        ↓
[Retorna dados do usuário]
        {
          "id_usuario": "1",
          "nome": "Gabriel Leal",
          "cpf_cnpj": "000.000.000-88",
          "email": "gabriel@email.com",
          "tipo_usuario": "CLIENTE",
          "id_cliente": 1,
          "id_conta": 1
        }
        ↓
[Salva dados no sessionStorage]
        ↓
[Redireciona para conta.html]
```

## 🧪 Como testar

### 1. Certifique-se de que a aplicação está rodando
```bash
mvnw spring-boot:run
```

### 2. Acesse a página de login
```
http://localhost:8080/index.html
```

### 3. Faça login com um usuário cadastrado
- Digite o CPF/CNPJ (será formatado automaticamente)
- Digite a senha
- Clique em "ENTRAR"

### 4. Verifique o redirecionamento
- Se sucesso: redireciona para `conta.html`
- Se erro: mostra modal com mensagem de erro

### 5. Verificar dados salvos (opcional)
Abra o DevTools (F12) → Console:
```javascript
// Ver dados do usuário logado
JSON.parse(sessionStorage.getItem('usuario'))

// Verificar se está logado
sessionStorage.getItem('isLoggedIn')
```

## 📊 Dados salvos no sessionStorage

Após login bem-sucedido, os seguintes dados são salvos:

```javascript
{
  id_usuario: "1",
  nome: "Gabriel Leal",
  cpf_cnpj: "000.000.000-88",
  email: "gabriel@email.com",
  tipo_usuario: "CLIENTE",
  id_cliente: 1,
  id_conta: 1
}
```

Esses dados podem ser acessados em qualquer página para:
- Mostrar nome do usuário
- Fazer requisições autenticadas
- Buscar informações da conta
- Controlar acesso a páginas

## 🔒 Segurança implementada

✅ **Senha criptografada com BCrypt** (não é armazenada em texto plano)  
✅ **Validação no backend** (CPF/CNPJ e senha)  
✅ **CORS configurado** para permitir apenas localhost  
✅ **Exceções customizadas** (UsuarioNaoEncontrado, SenhaIncorreta)  
✅ **Dados sensíveis no sessionStorage** (limpos ao fechar o navegador)

## ⚠️ Tratamento de Erros

### Frontend
- Campos vazios → validação HTML5
- CPF não encontrado → modal de erro
- Senha incorreta → modal de erro
- Erro de rede → modal de erro genérico

### Backend
- `UsuarioNaoEncontrado`: CPF/CNPJ não existe no banco
- `SenhaIncorreta`: Senha não confere

## 🎨 Funcionalidades do Frontend

1. **Máscara de CPF automática**: 000.000.000-00
2. **Toggle de senha**: Mostrar/ocultar senha
3. **Validação em tempo real**: Feedback visual
4. **Loading state**: Spinner durante login
5. **Modais responsivos**: Erro com Bootstrap
6. **Redirecionamento automático**: Após sucesso

## 🔄 Exemplo de uso com Postman

```http
POST http://localhost:8080/usuario/login
Content-Type: application/json

{
  "cpf_cnpj": "000.000.000-88",
  "senha": "12345678"
}
```

**Resposta de sucesso (200):**
```json
{
  "id_usuario": "1",
  "nome": "Gabriel Leal",
  "cpf_cnpj": "000.000.000-88",
  "email": "gabriel@email.com",
  "tipo_usuario": "CLIENTE",
  "id_cliente": 1,
  "id_conta": 1
}
```

**Resposta de erro (404):**
```json
{
  "timestamp": "2025-12-19T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "CPF/CNPJ não encontrado no sistema",
  "path": "/usuario/login"
}
```

**Resposta de erro (401):**
```json
{
  "timestamp": "2025-12-19T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Senha incorreta",
  "path": "/usuario/login"
}
```

## 📝 Próximos passos sugeridos

1. **Proteção de rotas**: Verificar se usuário está logado antes de acessar conta.html
2. **Token JWT**: Implementar autenticação baseada em token
3. **Logout**: Criar botão de logout que limpa sessionStorage
4. **Refresh token**: Manter sessão ativa
5. **Recuperação de senha**: Implementar "Esqueci a senha"
6. **Timeout de sessão**: Auto-logout após inatividade
7. **Lembrar-me**: Opção de manter login com localStorage

## 🚀 Status da Implementação

✅ Endpoint de login criado  
✅ Validação de credenciais com BCrypt  
✅ Frontend conectado à API  
✅ Redirecionamento automático  
✅ Tratamento de erros completo  
✅ Dados salvos no sessionStorage  
✅ UI/UX responsiva  

## 💡 Como usar dados do usuário em outras páginas

### Em conta.html, adicione no início do JavaScript:

```javascript
// Verificar se usuário está logado
const usuarioLogado = JSON.parse(sessionStorage.getItem('usuario'));
if (!usuarioLogado) {
    window.location.href = 'index.html';
}

// Usar dados do usuário
console.log('Bem-vindo,', usuarioLogado.nome);
console.log('ID da conta:', usuarioLogado.id_conta);

// Fazer requisições com o ID da conta
fetch(`http://localhost:8080/conta/${usuarioLogado.id_conta}`)
    .then(response => response.json())
    .then(data => {
        // Mostrar saldo, transações, etc.
    });
```

## 🎯 Teste completo do fluxo

1. Acesse: `http://localhost:8080/cadastro.html`
2. Crie um novo usuário
3. Volte para: `http://localhost:8080/index.html`
4. Faça login com o usuário criado
5. Veja o redirecionamento para `conta.html`
6. Abra o DevTools e veja os dados salvos

**Tudo pronto e funcionando! 🎉**

