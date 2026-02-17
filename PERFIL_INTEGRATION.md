# Integração Perfil.html com API - NexumBank

## 📋 Resumo da Implementação

Foi criado um sistema completo de integração entre a página de perfil (`perfil.html`) e as APIs REST do backend, permitindo que os dados do usuário sejam carregados automaticamente da base de dados e preenchidos na interface.

## 🆕 Arquivos Criados

### `perfil.js` - JavaScript de Integração com API

Este arquivo gerencia toda a lógica de comunicação com o backend:

#### Funcionalidades Principais:

1. **Verificação de Autenticação**
   - Verifica se o usuário está logado via `sessionStorage`
   - Redireciona para login se não autenticado

2. **Carregamento de Dados**
   - Busca dados do usuário via `GET /usuario/{id}`
   - Busca dados do cliente via `GET /cliente/{id}`
   - Busca dados da conta via `GET /conta/{id}`

3. **Preenchimento Automático**
   - Nome completo
   - CPF (formatado)
   - E-mail
   - Telefone
   - Data de nascimento
   - Profissão
   - Renda mensal
   - Endereço completo (extraído e separado em campos)
   - Número da agência
   - Número da conta

4. **Funções Utilitárias**
   - `formatarCPF()` - Formata CPF: 000.000.000-00
   - `formatarTelefone()` - Formata telefone
   - `formatarData()` - Converte data para dd/MM/yyyy
   - `formatarMoeda()` - Formata valores monetários
   - `extrairEndereco()` - Separa endereço completo em partes

## 🔄 Fluxo de Dados

```
[Usuário acessa perfil.html]
        ↓
[Verifica sessionStorage]
        ↓
[Se não logado → Redireciona para index.html]
        ↓
[Se logado → Busca dados]
        ↓
┌─────────────────────────────────┐
│ GET /usuario/{id_usuario}       │
│ GET /cliente/{id_cliente}       │
│ GET /conta/{id_conta}           │
└─────────────────────────────────┘
        ↓
[Dados retornados em JSON]
        ↓
[Preenche formulários]
        ↓
[Página exibida com dados reais]
```

## 📊 Estrutura de Dados

### Dados armazenados no sessionStorage (do login):
```javascript
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

### Resposta da API - GET /usuario/{id}:
```json
{
  "id_usuario": "1",
  "nome": "Gabriel Leal",
  "cpf_cnpj": "000.000.000-88",
  "email": "gabriel@email.com",
  "telefone": "(88) 98800-1122",
  "endereco": "Rua Exemplo, 123 - Centro, Penaforte - CE - CEP: 63000-000",
  "estado": "CE",
  "tipoUsuario": "CLIENTE",
  "data_nascimento": "19/12/2000"
}
```

### Resposta da API - GET /cliente/{id}:
```json
{
  "id": 1,
  "profissao": "Desenvolvedor",
  "renda_mensal": 5000.00,
  "usuario": { /* UsuarioResponseDTO */ }
}
```

### Resposta da API - GET /conta/{id}:
```json
{
  "id_conta": 1,
  "numero_conta": "123456-7",
  "agencia": "0001",
  "saldo": 1500.00,
  "status_conta": "ATIVA",
  "data_criacao": "2025-01-15T10:30:00"
}
```

## 🎯 Campos Preenchidos Automaticamente

### Informações Pessoais:
- ✅ Nome Completo
- ✅ CPF (somente leitura)
- ✅ E-mail
- ✅ Telefone
- ✅ Data de Nascimento
- ✅ Profissão
- ✅ Renda Mensal

### Endereço:
- ✅ CEP
- ✅ Logradouro
- ✅ Número
- ✅ Complemento
- ✅ Bairro
- ✅ Cidade
- ✅ Estado

### Informações da Conta:
- ✅ Agência
- ✅ Número da Conta

### Sidebar:
- ✅ Nome do Usuário

## 🔧 Como Funciona o Salvamento (Preparado)

A função `salvarPerfil()` está preparada para enviar dados de volta à API:

```javascript
async function salvarPerfil() {
    // PUT /usuario/{id}
    const dados = {
        nome: "Novo Nome",
        cpf_cnpj: "000.000.000-88",
        email: "novo@email.com",
        telefone: "(88) 99999-9999",
        endereco: "Endereço completo",
        estado: "CE",
        tipo_usuario: "CLIENTE",
        data_nascimento: "19/12/2000",
        profissao: "Nova Profissão",
        renda_mensal: 6000.00
    };
    
    // Faz PUT para atualizar
}
```

## 🧪 Como Testar

### 1. Fazer Login
```
1. Acesse http://localhost:8080/index.html
2. Faça login com um usuário cadastrado
3. Após login, será redirecionado para conta.html
```

### 2. Acessar Perfil
```
4. Clique no link "Perfil" na sidebar
5. Ou acesse diretamente: http://localhost:8080/perfil.html
```

### 3. Verificar Dados
```
6. Os campos devem ser preenchidos automaticamente
7. Abra DevTools (F12) → Console para ver os logs
8. Verifique: "Dados carregados: {usuario, cliente, conta}"
```

### 4. Debug no Console
```javascript
// Ver dados do usuário logado
JSON.parse(sessionStorage.getItem('usuario'))

// Ver dados carregados (após abrir perfil)
// Os dados são exibidos no console.log
```

## 🚀 Funcionalidades Extras

### 1. **Proteção de Rota**
Se o usuário não estiver logado, é automaticamente redirecionado para `index.html`.

### 2. **Loading State**
Durante o carregamento dos dados, a página fica com opacidade reduzida.

### 3. **Notificações**
Sistema de notificações via Bootstrap alerts para feedback ao usuário.

### 4. **Extração Inteligente de Endereço**
O endereço completo armazenado como string é automaticamente separado em:
- CEP
- Logradouro
- Número
- Complemento
- Bairro
- Cidade

### 5. **Formatação Automática**
- CPF: 000.000.000-00
- Moeda: R$ 5.000,00
- Data: dd/MM/yyyy

## ⚠️ Observações Importantes

### 1. **Dependências de sessionStorage**
A página depende dos dados salvos no login:
- `id_usuario` - Obrigatório
- `id_cliente` - Opcional (somente para clientes)
- `id_conta` - Opcional

### 2. **Formato de Endereço**
O endereço deve estar no formato:
```
"Logradouro, Número - Complemento - Bairro, Cidade - CEP: 00000-000"
```

### 3. **Data de Nascimento**
Pode estar em dois formatos:
- `dd/MM/yyyy` (do banco)
- `yyyy-MM-dd` (input HTML)

A função `formatarDataInput()` faz a conversão automaticamente.

## 🔒 Segurança

- ✅ Verificação de autenticação antes de carregar dados
- ✅ Redirecionamento automático se não autenticado
- ✅ CPF não pode ser editado
- ✅ Logout limpa sessionStorage

## 📝 Próximos Passos

1. **Implementar salvamento de endereço**
   - Função `saveAddress()` está preparada
   - Precisa enviar PUT para atualizar endereço

2. **Adicionar upload de foto**
   - Botão "Alterar Foto" já existe
   - Implementar upload e preview

3. **Implementar edição de profissão e renda**
   - Endpoint já existe: `PUT /cliente/profissao-renda`
   - Integrar com formulário

4. **Adicionar validações**
   - Validar formato de telefone
   - Validar formato de e-mail
   - Validar renda mensal

## 🎨 Exemplo de Uso Completo

```javascript
// 1. Usuário faz login
// → sessionStorage armazenado

// 2. Usuário acessa perfil.html
// → perfil.js carrega automaticamente

// 3. Dados são buscados da API:
inicializarPagina()
  → verificarAutenticacao()
  → carregarDadosUsuario(1)
  → carregarDadosCliente(1)
  → carregarDadosConta(1)
  → preencherDadosPerfil(usuario, cliente, conta)

// 4. Página exibe dados reais do banco!
```

## ✅ Status da Implementação

✅ Verificação de autenticação  
✅ Carregamento de dados do usuário  
✅ Carregamento de dados do cliente  
✅ Carregamento de dados da conta  
✅ Preenchimento automático de todos os campos  
✅ Formatação de CPF, telefone, data e moeda  
✅ Extração e separação de endereço  
✅ Sistema de notificações  
✅ Proteção de rota  
✅ Logout funcional  
⏳ Salvamento de alterações (preparado, não implementado)  
⏳ Upload de foto (botão criado, não implementado)  

**A integração do perfil com a API está completa e funcional! 🎉**

