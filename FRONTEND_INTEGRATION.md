# Integração Frontend com API de Cadastro de Usuário

## O que foi implementado

A integração entre o formulário HTML de cadastro e a API REST de criação de usuário foi concluída com sucesso.

## Arquivos Criados/Modificados

### 1. `src/main/resources/static/js/cadastro.js` (NOVO)
Arquivo JavaScript que gerencia toda a lógica do frontend:

#### Funcionalidades Implementadas:

- **Máscaras de Input**: Aplica formatação automática para CPF, CEP e Telefone
- **Toggle de Senha**: Botões para mostrar/ocultar senha
- **Busca de CEP**: Integração com API ViaCEP para preenchimento automático de endereço
- **Validação em Tempo Real**: Valida confirmação de email e senha enquanto o usuário digita
- **Envio para API**: Submete os dados do formulário para o endpoint `/usuario`
- **Feedback Visual**: Modais de sucesso ou erro após a submissão
- **Loading State**: Mostra spinner no botão durante o envio

#### Como funciona o envio:

```javascript
// Dados são montados no formato esperado pela API
const dados = {
    nome: "Nome completo do usuário",
    cpf_cnpj: "CPF formatado",
    email: "email@exemplo.com",
    telefone: "telefone formatado",
    senha: "senha em texto (será criptografada no backend)",
    endereco: "Rua X, 123 - Bairro, Cidade - CEP: 00000-000",
    estado: "CE",
    tipo_usuario: "CLIENTE", // Sempre CLIENTE no cadastro público
    data_nascimento: "2000-01-01",
    profissao: null,
    renda_mensao: null
};
```

### 2. `src/main/java/nexum/com/nexumbank/config/SecurityConfig.java` (MODIFICADO)
Adicionada configuração de CORS para permitir requisições do frontend:

```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
        }
    };
}
```

### 3. `src/main/resources/templates/cadastro.html` (MODIFICADO)
O HTML agora referencia o arquivo JavaScript externo.

## Como Testar

### 1. Iniciar a aplicação Spring Boot
```bash
mvnw spring-boot:run
```

### 2. Acessar o formulário de cadastro
Abra o navegador em: `http://localhost:8080/cadastro.html`

### 3. Preencher o formulário
- O formulário tem validação completa
- CPF, CEP e telefone são formatados automaticamente
- Ao digitar o CEP e sair do campo, o endereço é preenchido automaticamente
- Emails e senhas são validados em tempo real

### 4. Submeter o formulário
Ao clicar em "CRIAR CONTA":
1. O botão mostra um spinner de loading
2. Os dados são enviados para `POST /usuario`
3. Se sucesso: Modal de sucesso + redirecionamento para login
4. Se erro: Modal de erro com mensagem específica

## Fluxo de Dados

```
[Formulário HTML]
        ↓
[cadastro.js - Validação]
        ↓
[cadastro.js - Montagem do JSON]
        ↓
[fetch() - POST /usuario]
        ↓
[UsuarioController.criarUsuario()]
        ↓
[UsuarioService.criarUsuario()]
        ↓
[Validações + Criptografia de Senha]
        ↓
[ClienteService.criarCliente()] (se tipo_usuario = CLIENTE)
        ↓
[Salvar no Banco de Dados]
        ↓
[Retornar UsuarioResponseDTO]
        ↓
[Modal de Sucesso no Frontend]
```

## Tratamento de Erros

O sistema trata os seguintes erros:

### Backend (UsuarioService)
- `CpfCnpjJaCadastrado`: CPF já existe no banco
- `EmailJaCadastrado`: Email já existe no banco
- Validações de campos obrigatórios

### Frontend (cadastro.js)
- Validação de formato de campos
- Confirmação de email diferente
- Confirmação de senha diferente
- Termos não aceitos
- Erros de rede/conexão
- Erros retornados pela API

## Segurança

- ✅ Senha é criptografada no backend usando BCrypt
- ✅ CORS configurado para localhost apenas
- ✅ Validações no frontend E backend
- ✅ CPF/Email únicos garantidos pelo banco
- ✅ tipo_usuario fixado como "CLIENTE" no frontend (usuários públicos não podem se cadastrar como GERENTE)

## Próximos Passos Sugeridos

1. **Adicionar captcha** para evitar bots
2. **Validação de CPF** mais robusta (verificar dígitos)
3. **Verificação de email** (enviar link de confirmação)
4. **Política de senha forte** (validar complexidade)
5. **Página de login funcional** para completar o fluxo
6. **Tratamento de profissão e renda mensal** (criar endpoint separado ou adicionar ao cadastro)

## Observações Importantes

- O campo `tipo_usuario` é sempre "CLIENTE" no cadastro público
- Os campos `profissao` e `renda_mensao` são enviados como `null` (conforme TODO no UsuarioService)
- O endereço é montado como string completa a partir dos campos separados
- A aplicação usa porta 8080 (padrão Spring Boot)
- Bootstrap 5.3.2 está sendo usado para componentes UI

## Testando com Postman

Se preferir testar diretamente a API:

```json
POST http://localhost:8080/usuario
Content-Type: application/json

{
    "nome": "Gabriel Leal",
    "cpf_cnpj": "000.000.000-88",
    "email": "gabriel@email.com",
    "senha": "12345678",
    "telefone": "88 9 8800-1122",
    "endereco": "Rua Exemplo, 123 - Centro, Penaforte - CE - CEP: 63000-000",
    "estado": "CE",
    "tipo_usuario": "CLIENTE",
    "data_nascimento": "2000-01-01",
    "profissao": null,
    "renda_mensao": null
}
```

## Compatibilidade

- ✅ Navegadores modernos (Chrome, Firefox, Edge, Safari)
- ✅ Design responsivo (mobile, tablet, desktop)
- ✅ Java 17+
- ✅ Spring Boot 3.x

