const API_BASE_URL = 'http://localhost:8080';

function verificarAutenticacao() {
    const usuario = JSON.parse(sessionStorage.getItem('usuario'));
    if (!usuario) {
        window.location.href = 'index.html';
        return null;
    }
    return usuario;
}

async function carregarDadosUsuario(idUsuario) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuario/${idUsuario}`);
        if (!response.ok) {
            throw new Error('Erro ao buscar dados do usuário');
        }
        return await response.json();
    } catch (error) {
        console.error('Erro ao carregar dados do usuário:', error);
        showNotification('Erro ao carregar dados do usuário', 'danger');
        return null;
    }
}

async function carregarDadosCliente(idCliente) {
    try {
        const response = await fetch(`${API_BASE_URL}/cliente/${idCliente}`);
        if (!response.ok) {
            throw new Error('Erro ao buscar dados do cliente');
        }
        return await response.json();
    } catch (error) {
        console.error('Erro ao carregar dados do cliente:', error);
        return null;
    }
}

async function carregarDadosConta(idConta) {
    try {
        const response = await fetch(`${API_BASE_URL}/conta/${idConta}`);
        if (!response.ok) {
            throw new Error('Erro ao buscar dados da conta');
        }
        return await response.json();
    } catch (error) {
        console.error('Erro ao carregar dados da conta:', error);
        return null;
    }
}

function formatarCPF(cpf) {
    if (!cpf) return '';
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function formatarTelefone(telefone) {
    if (!telefone) return '';
    return telefone;
}

function formatarData(data) {
    if (!data) return '';
    const [ano, mes, dia] = data.split('-');
    return `${ano}-${mes}-${dia}`;
}

function formatarDataInput(data) {
    if (!data) return '';
    const [dia, mes, ano] = data.split('/');
    return `${ano}-${mes}-${dia}`;
}

function formatarMoeda(valor) {
    if (!valor && valor !== 0) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

function extrairEndereco(enderecoCompleto) {
    if (!enderecoCompleto) return {};

    const partes = {
        logradouro: '',
        numero: '',
        complemento: '',
        bairro: '',
        cidade: '',
        cep: ''
    };

    try {

        const campos = enderecoCompleto.split(',').map((item => item.trim()));

        partes.logradouro = campos[0] || '';
        partes.numero = campos[1] || '';
        partes.complemento = campos[2] || '';
        partes.bairro = campos[3] || '';
        partes.cidade = campos[4] || '';
        partes.cep = campos[5] || '';

        console.log(campos[5])

    } catch (error) {
        console.error('Erro ao extrair endereço:', error);
    }

    return partes;
}

function preencherDadosPerfil(usuario, cliente, conta) {
    document.querySelectorAll('.user-info .fw-semibold').forEach(el => {
        el.textContent = usuario.nome;
    });

    const nomePartes = usuario.nome.split(' ');
    const iniciais = (nomePartes[0]?.charAt(0) || '') + (nomePartes[nomePartes.length - 1]?.charAt(0) || '');

    document.querySelector('.profile-picture + h4').textContent = usuario.nome;

    if (conta) {
        document.querySelector('.account-info .col-4 .fw-bold').textContent = conta.agencia;
        document.querySelector('.account-info .col-8 .fw-bold').textContent = conta.numero_conta;
        const accountStatus = document.querySelector('small[id="accountStatus"]');
        if (accountStatus) {
            const status = conta.status_conta;
            console.log(status);
            if (status.includes('Ativo')) {
                accountStatus.style.color = '#28ff28';
                accountStatus.textContent = conta.status_conta;
            } else if (status.includes('Bloqueado')) {
                accountStatus.style.color = '#ff6868';
                accountStatus.textContent = conta.status_conta;
            } else {
                accountStatus.style.color = 'gray';
            }
        }

        document.querySelector('small[id="accountStatus"]').textContent = conta.status_conta;

    }

    const form = document.getElementById('profileForm');
    if (form) {
        form.querySelector('input[value="nome"]').value = usuario.nome;

        form.querySelector('input[value="cpf"]').value = formatarCPF(usuario.cpf_cnpj);

        form.querySelector('input[value="email"]').value = usuario.email;

        form.querySelector('input[value="telefone"]').value = formatarTelefone(usuario.telefone);

        if (usuario.data_nascimento) {
            //TODO deletar linha 156
            console.log(usuario.data_nascimento)
            const dataInput = form.querySelector('input[type="date"]');
            if (dataInput) {
                dataInput.value = formatarData(usuario.data_nascimento);
            }
        }

        if (cliente?.profissao) {
            form.querySelector('input[value="profissao"]').value = cliente.profissao;
        } else {
            form.querySelector('input[value="profissao"]').value = "Não especificado";
        }

        if (cliente?.renda_mensal) {
            form.querySelector('input[value="rendaMensal"]').value = formatarMoeda(cliente.renda_mensal);
        } else {
            form.querySelector('input[value="rendaMensal"]').value = "R$ 0,00";
        }
    }

    if (usuario.endereco) {
        const enderecoParts = extrairEndereco(usuario.endereco);
        const addressForm = document.getElementById('addressForm');

        if (addressForm) {
            const inputs = addressForm.querySelectorAll('input');

            if (inputs[0]) inputs[0].value = enderecoParts.logradouro || '';

            if (inputs[1]) inputs[1].value = enderecoParts.numero || '';

            if (inputs[2]) inputs[2].value = enderecoParts.complemento || '';

            if (inputs[3]) inputs[3].value = enderecoParts.bairro || '';

            if (inputs[4]) inputs[4].value = enderecoParts.cidade || '';

            if (inputs[5]) inputs[5].value = enderecoParts.cep || '';

            if (inputs[6] && usuario.estado) {
                inputs[6].value = `${usuario.estado}`;
            }
        }
    }
}

async function inicializarPagina() {
    document.body.style.opacity = '0.7';

    const usuarioLogado = verificarAutenticacao();
    if (!usuarioLogado) return;

    try {
        const usuario = await carregarDadosUsuario(usuarioLogado.id_usuario);
        if (!usuario) {
            showNotification('Erro ao carregar dados do usuário', 'danger');
            return;
        }

        let cliente = null;
        if (usuarioLogado.id_cliente) {
            cliente = await carregarDadosCliente(usuarioLogado.id_cliente);
        }

        let conta = null;
        if (usuarioLogado.id_conta) {
            conta = await carregarDadosConta(usuarioLogado.id_conta);
        }

        preencherDadosPerfil(usuario, cliente, conta);

        console.log('Dados carregados:', {usuario, cliente, conta});

    } catch (error) {
        console.error('Erro ao inicializar página:', error);
        showNotification('Erro ao carregar dados do perfil', 'danger');
    } finally {
        document.body.style.opacity = '1';
    }
}

async function editarInformacoesPessoais() {
    const usuarioLogado = verificarAutenticacao();
    if (!usuarioLogado) return;

    const form = document.getElementById('profileForm');
    const saveButton = document.querySelector('button[onclick="saveProfile()"]');
    const originalText = saveButton.innerHTML;

    saveButton.innerHTML = '<i class="bi bi-hourglass-split me-1"></i>Salvando...';
    saveButton.disabled = true;

    let enderecoAtual = usuarioLogado.endereco || '';
    const addressForm = document.getElementById('addressForm');
    if (addressForm) {
        addresInputs = addressForm.querySelectorAll('input, select');
        const logradouro = (addresInputs[0]?.value || '').trim();
        const numero = (addresInputs[1]?.value || '').trim();
        const complemento = (addresInputs[2]?.value || '').trim();
        const bairro = (addresInputs[3]?.value || '').trim();
        const cidade = (addresInputs[4]?.value || '').trim();
        const cep = (addresInputs[5]?.value || '').trim();
        // const estado = (addresInputs[6]?.value || '').trim();

        const partes = [logradouro, numero, complemento, bairro, cidade, cep].filter(p => p.length);
        if (partes.length) {
            enderecoAtual = partes.join(', ');
        }
    }
    console.log("Endereço: " + enderecoAtual)

    try {
        const inputs = form.querySelectorAll('input, select');
        const dados = {
            nome: inputs[0].value,
            telefone: inputs[3].value,
            endereco: enderecoAtual,
        };

        const response = await fetch(`${API_BASE_URL}/usuario/${usuarioLogado.id_usuario}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            throw new Error('Erro ao atualizar perfil');
        }

        saveButton.innerHTML = '<i class="bi bi-check-circle me-1"></i>Salvo!';

        setTimeout(() => {
            cancelEdit();
            showNotification('Informações atualizadas com sucesso!', 'success');
            inicializarPagina();
        }, 1000);

    } catch (error) {
        console.error('Erro ao salvar perfil:', error);
        showNotification('Erro ao salvar alterações', 'danger');
        saveButton.innerHTML = originalText;
        saveButton.disabled = false;
    }
}

function applyPhoneMask(phone) {
    return phone.replace(/\D/g, '')
        .replace(/(\d{2})(\d)/, '($1) $2')
        .replace(/(\d{4})(\d)/, '$1-$2')
        .replace(/(\d{4})-(\d)(\d{4})/, '$1$2-$3')
        .replace(/(-\d{4})\d+?$/, '$1');
}

function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    notification.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'info' ? 'info-circle' : type === 'danger' ? 'x-circle' : 'exclamation-triangle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 5000);
}

function confirmLogout() {
    sessionStorage.clear();
    showNotification('Saindo da conta...', 'info');
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 1500);
}

document.addEventListener('DOMContentLoaded', inicializarPagina);
document.addEventListener('DOMContentLoaded', function(){
    document.getElementById('telefone').addEventListener('input', function(e) {
        e.target.value = applyPhoneMask(e.target.value);
    });
});

