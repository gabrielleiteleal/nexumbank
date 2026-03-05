const API_BASE_URL = 'http://localhost:8080';

const sidebarToggle = document.getElementById('sidebarToggle');
const sidebar = document.querySelector('.sidebar');
const overlay = document.getElementById('sidebarOverlay');

const usuarioLogado = verificarAutenticacao();

function verificarAutenticacao() {
    const usuario = JSON.parse(sessionStorage.getItem('usuario'));
    if (!usuario) {
        window.location.href = 'index.html';
        return null;
    }
    return usuario;
}

async function carregarDadosUsuario(idUsuario) {
    const response = await fetch(`${API_BASE_URL}/usuario/${idUsuario}`);
    if (!response.ok) {
        const errorResponse = await response.json();
        showNotification('Erro ao carregar dados do usuário', 'danger');
        throw new Error('Erro ao buscar dados do usuário' + errorResponse.message);
    }
    return await response.json();
}

async function carregarDadosCliente(idCliente) {
    const response = await fetch(`${API_BASE_URL}/cliente/${idCliente}`);
    if (!response.ok) {
        const errorResponse = await response.json();
        showNotification('Erro ao carregar dados do cliente', 'danger');
        throw new Error('Erro ao buscar dados do cliente' + errorResponse.message);
    }
    return await response.json();
}

async function carregarDadosConta(idConta) {
    const response = await fetch(`${API_BASE_URL}/conta/${idConta}`);
    if (!response.ok) {
        const errorResponse = await response.json();
        showNotification('Erro ao carregar dados da conta', 'danger');
        throw new Error('Erro ao buscar dados da conta' + errorResponse.message);
    }
    return await response.json();
}

async function inicializarPagina() {

    document.body.style.opacity = '0.7';
    try {
        if (!usuarioLogado) {
            return;
        }

        const usuario = await carregarDadosUsuario(usuarioLogado["id_usuario"]);
        const cliente = await carregarDadosCliente(usuarioLogado["id_usuario"]);
        const conta = await carregarDadosConta(usuarioLogado["id_usuario"]);
        console.log(usuario, cliente, conta);

    } catch (erro) {
        console.error('Erro ao inicializar página:', erro);
        showNotification('Erro ao carregar dados do usuário', 'danger');
    } finally {
        document.body.style.opacity = '1';
    }
}

function logout() {
    const logoutModal = new bootstrap.Modal(document.getElementById('logoutModal'));
    logoutModal.show();
}

function confirmLogout() {
    sessionStorage.clear();
    showNotification('Saindo da conta...', 'info');
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 1500);
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

function toggleSidebar() {
    sidebar.classList.toggle('show');
    overlay.classList.toggle('show');
    document.body.classList.toggle('sidebar-open');
}

function closeSidebar() {
    sidebar.classList.remove('show');
    overlay.classList.remove('show');
    document.body.classList.remove('sidebar-open');
}

function setupToggleVisibility(toggleId, valueId, hiddenId, iconId) {
    const toggleBtn = document.getElementById(toggleId);
    const valueElement = document.getElementById(valueId);
    const hiddenElement = document.getElementById(hiddenId);
    const iconElement = document.getElementById(iconId);

    if (toggleBtn && valueElement && hiddenElement && iconElement) {
        toggleBtn.addEventListener('click', function () {
            const isVisible = !valueElement.classList.contains('d-none');

            if (isVisible) {
                valueElement.classList.add('d-none');
                hiddenElement.classList.remove('d-none');
                iconElement.classList.remove('bi-eye');
                iconElement.classList.add('bi-eye-slash');
            } else {
                valueElement.classList.remove('d-none');
                hiddenElement.classList.add('d-none');
                iconElement.classList.remove('bi-eye-slash');
                iconElement.classList.add('bi-eye');
            }

            toggleBtn.style.transform = 'scale(0.9)';
            setTimeout(() => {
                toggleBtn.style.transform = 'scale(1)';
            }, 150);
        });
    }
}

async function loadBalanceAndAgencyNumber() {
    const balanceElement = document.getElementById('saldoValue');
    const agencyNumberElement = document.getElementById('agencyNumber');
    const conta = await carregarDadosConta(usuarioLogado["id_usuario"]);
    if (!conta) return;

    try {
        balanceElement.textContent = formatCurrency(conta.saldo);
    } catch (error) {
        console.error('Erro ao carregar saldo:', error);
        balanceElement.textContent = 'R$ 0,00';
    }

    try {
        agencyNumberElement.textContent = conta["agencia"];
    } catch (error) {
        console.error('Erro ao carregar agência:', error);
        agencyNumberElement.textContent = '000-0';
    }
}

async function loadClientInformation() {
    if (!usuarioLogado) return;
    document.getElementById('nome').textContent = usuarioLogado.nome;
}

function parseBRLToNumber(brl) {
    if (!brl) return 0;
    const cleaned = brl.replace(/[^.\d,]/g, '').replace(/\./g, '');
    const normalized = cleaned.replace(/,/g, '.');
    const parsed = parseFloat(normalized);
    return Number.isNaN(parsed) ? 0 : parsed;
}

function applyCPFMask(cpf) {
    return cpf.replace(/\D/g, '')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})/, '$1-$2')
        .replace(/(-\d{2})\d+?$/, '$1');
}

function applyCNPJMask(cnpj) {
    return cnpj.replace(/\D/g, '')
        .replace(/(\d{2})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1/$2')
        .replace(/(\d{4})(\d{1,2})/, '$1-$2')
        .replace(/(-\d{2})\d+?$/, '$1');
}

function applyCPFCNPJMask(value) {
    const digits = (value || '').replace(/\D/g, '');
    if (digits.length > 11) {
        return applyCNPJMask(value);
    }
    return applyCPFMask(value);
}

sidebarToggle?.addEventListener('click', toggleSidebar);
overlay?.addEventListener('click', closeSidebar);

window.addEventListener('DOMContentLoaded', () => {
    const addBalanceBtn = document.getElementById('addBalanceBtn');
    const addBalanceModalElem = document.getElementById('addBalanceModal');
    const addBalanceValueInput = document.getElementById('addBalanceValue');
    const addBalanceForm = document.getElementById('addBalanceForm');

    let addBalanceModal = null;
    if (addBalanceModalElem) addBalanceModal = new bootstrap.Modal(addBalanceModalElem);

    if (addBalanceBtn && addBalanceModal) {
        addBalanceBtn.addEventListener('click', () => {
            if (addBalanceValueInput) addBalanceValueInput.value = '';
            addBalanceModal.show();
        });
    }

    if (addBalanceValueInput) {
        addBalanceValueInput.addEventListener('input', (e) => {
            const raw = e.target.value;
            const digits = raw.replace(/\D/g, '');
            if (digits.length === 0) {
                e.target.value = '';
                return;
            }
            const cents = digits.padStart(3, '0');
            const integerPart = cents.slice(0, -2);
            const decimalPart = cents.slice(-2);
            e.target.value = new Intl.NumberFormat('pt-BR').format(Number(integerPart)) + ',' + decimalPart;
        });

        addBalanceValueInput.addEventListener('paste', (e) => {
            e.preventDefault();
            const text = (e.clipboardData || window.clipboardData).getData('text');
            e.target.value = text.replace(/\D/g, '');
        });
    }

    if (addBalanceForm) {
        addBalanceForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            const id_cliente = usuarioLogado.id_cliente;
            if (!usuarioLogado) throw new Error('Usuário não autenticado');
            if (!id_cliente) throw new Error('Cliente não vinculado ao usuário');

            try {
                const rawValue = addBalanceValueInput ? addBalanceValueInput.value : '';
                const amount = parseBRLToNumber(rawValue);
                if (!amount || amount <= 0) {
                    showNotification('Digite um valor válido para adicionar', 'danger');
                    return;
                }

                const dados = {
                    id_cliente: id_cliente,
                    saldo: amount
                };

                const response = await fetch(`${API_BASE_URL}/conta/depositar`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(dados)
                });

                if (!response.ok) {
                    const err = await response.json().catch(() => ({}));
                    const message = err.message || 'Erro ao adicionar saldo';
                    showNotification(message, 'danger');
                    return;
                }

                const result = await response.json();
                if (result === true) {
                    showNotification('Saldo adicionado com sucesso', 'success');
                    if (addBalanceModal) addBalanceModal.hide();
                    await loadBalanceAndAgencyNumber();
                    await loadClientInformation();
                } else {
                    showNotification('Não foi possível adicionar o saldo', 'danger');
                }

            } catch (error) {
                console.error('Erro ao adicionar saldo:', error);
                showNotification(error.message || 'Erro ao adicionar saldo', 'danger');
            }
        });
    }

});

window.addEventListener('DOMContentLoaded', () => {

    const transferBtns = document.querySelectorAll('.action-btn');
    const transferModalElem = document.getElementById('transferModal');
    const transferKeyTypeSelect = document.getElementById('transferKeyType');
    const transferKeyInput = document.getElementById('transferKey');
    const transferValueInput = document.getElementById('transferValue');
    const transferForm = document.getElementById('transferForm');

    let transferModal = null;
    if (transferModalElem) transferModal = new bootstrap.Modal(transferModalElem);

    if (transferBtns.length > 0 && transferModal) {
        transferBtns[0].addEventListener('click', () => {
            if (transferKeyTypeSelect) transferKeyTypeSelect.value = '';
            if (transferKeyInput) transferKeyInput.value = '';
            if (transferValueInput) transferValueInput.value = '';
            transferModal.show();
        });
    }

    if (transferValueInput) {
        transferValueInput.addEventListener('input', (e) => {
            const raw = e.target.value;
            const digits = raw.replace(/\D/g, '');
            if (digits.length === 0) {
                e.target.value = '';
                return;
            }
            const cents = digits.padStart(3, '0');
            const integerPart = cents.slice(0, -2);
            const decimalPart = cents.slice(-2);
            e.target.value = new Intl.NumberFormat('pt-BR').format(Number(integerPart)) + ',' + decimalPart;
        });

        transferValueInput.addEventListener('paste', (e) => {
            e.preventDefault();
            const text = (e.clipboardData || window.clipboardData).getData('text');
            e.target.value = text.replace(/\D/g, '');
        });
    }

    if (transferKeyTypeSelect && transferKeyInput) {
        transferKeyTypeSelect.addEventListener('change', () => {
            const keyType = transferKeyTypeSelect.value;
            transferKeyInput.value = '';

            transferKeyInput.oninput = null;
            transferKeyInput.onpaste = null;

            switch (keyType) {
                case 'CPF':
                    transferKeyInput.placeholder = '000.000.000-00';
                    transferKeyInput.maxLength = 18;
                    transferKeyInput.oninput = function (e) {
                        e.target.value = applyCPFCNPJMask(e.target.value);
                    };
                    transferKeyInput.onpaste = function (e) {
                        e.preventDefault();
                        const text = (e.clipboardData || window.clipboardData).getData('text');
                        e.target.value = applyCPFCNPJMask(text);
                    };
                    break;
                case 'CNPJ':
                    transferKeyInput.placeholder = '00.000.000/0000-00';
                    transferKeyInput.maxLength = 20;
                    transferKeyInput.oninput = function (e) {
                        e.target.value = applyCPFCNPJMask(e.target.value);
                    };
                    transferKeyInput.onpaste = function (e) {
                        e.preventDefault();
                        const text = (e.clipboardData || window.clipboardData).getData('text');
                        e.target.value = applyCPFCNPJMask(text);
                    };
                    break;
                case 'EMAIL':
                    transferKeyInput.placeholder = 'exemplo@email.com';
                    transferKeyInput.maxLength = 100;
                    break;
                case 'TELEFONE':
                    transferKeyInput.placeholder = '(00) 00000-0000';
                    transferKeyInput.maxLength = 15;
                    break;
                case 'CONTA':
                    transferKeyInput.placeholder = 'Número da conta';
                    transferKeyInput.maxLength = 20;
                    break;
                default:
                    transferKeyInput.placeholder = 'Digite a chave do destinatário';
            }
        });
    }

    async function buscarContaPorChave(chave, tipoChave) {

        const response = await fetch(`${API_BASE_URL}/cliente`);
        if (!response.ok) throw new Error('Erro ao buscar clientes');
        const clientes = await response.json();

        try {
            let clienteEncontrado = null;

            switch (tipoChave) {
                case 'CPF':
                    const normalizedKey = (chave || '').toString().replace(/\D/g, '').trim();
                    clienteEncontrado = clientes.find(c => {
                        const cand = c?.usuario?.cpf_cnpj;
                        if (!cand) return false;
                        const normalized = String(cand).replace(/\D/g, '').trim();
                        return normalized !== '' && normalized === normalizedKey;
                    }) || null;

                    console.log('Cliente encontrado na lista: ', clienteEncontrado);
                    break;
                case 'EMAIL':
                    clienteEncontrado = clientes.find(c => c.email === chave);
                    break;
                case 'TELEFONE':
                    clienteEncontrado = clientes.find(c => c.telefone === chave);
                    break;
                case 'CONTA':
                    const contasResponse = await fetch(`${API_BASE_URL}/conta`);
                    const contas = await contasResponse.json();
                    const conta = contas.find(c => c["numero_conta"] === chave);
                    return conta ? conta["id_conta"] : null;
            }

            if (!clienteEncontrado) return null;

            const contasResponse = await fetch(`${API_BASE_URL}/conta`);
            const contas = await contasResponse.json();
            const clientId = clienteEncontrado?.id;


            const contaCliente = contas.find(c =>
                String(c["id_conta"]).trim() === String(clientId ?? '').trim()
            );

            return contaCliente ? contaCliente["id_conta"] : null;
        } catch (error) {
            console.error('Erro ao buscar conta:', error);
            return null;
        }
    }

    if (transferForm) {
        transferForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();

            const submitBtn = document.getElementById('transferSubmit');
            if (submitBtn) submitBtn.disabled = true;

            const usuario = verificarAutenticacao();
            if (!usuario) throw new Error('Usuário não autenticado');
            if (!usuario["id_conta"]) throw new Error('Conta não encontrada');

            try {
                const keyType = transferKeyTypeSelect ? transferKeyTypeSelect.value : '';
                const key = transferKeyInput ? transferKeyInput.value.trim() : '';
                const rawValue = transferValueInput ? transferValueInput.value : '';
                const amount = parseBRLToNumber(rawValue);

                if (!keyType) {
                    showNotification('Selecione o tipo de chave', 'danger');
                    return;
                }

                if (!key) {
                    showNotification('Digite a chave do destinatário', 'danger');
                    return;
                }

                if (!amount || amount <= 0) {
                    showNotification('Digite um valor válido para transferir', 'danger');
                    return;
                }

                showNotification('Buscando destinatário...', 'info');
                const contaDestino = await buscarContaPorChave(key, keyType);

                if (!contaDestino) {
                    showNotification('Destinatário não encontrado', 'danger');
                    return;
                }

                if (contaDestino === usuario["id_conta"]) {
                    showNotification('Você não pode transferir para si mesmo', 'danger');
                    return;
                }

                const payload = {
                    tipo_transacao: 'TRANSFERENCIA',
                    valor: amount,
                    conta_origem: usuario["id_conta"],
                    conta_destino: contaDestino
                };

                const response = await fetch(`${API_BASE_URL}/transacao/transferir`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    const err = await response.json().catch(() => ({}));
                    const message = err.message || 'Erro ao realizar transferência';
                    showNotification(message, 'danger');
                    return;
                }

                const result = await response.json();
                if (result) {
                    showNotification('Transferência realizada com sucesso!', 'success');
                    if (transferModal) transferModal.hide();
                    await loadBalanceAndAgencyNumber();
                } else {
                    showNotification('Não foi possível realizar a transferência', 'danger');
                }

            } catch (error) {
                console.error('Erro ao transferir:', error);
                showNotification(error.message || 'Erro ao realizar transferência', 'danger');
            } finally {
                if (submitBtn) submitBtn.disabled = false;
            }
        });
    }

});

setupToggleVisibility('toggleSaldo', 'saldoValue', 'saldoHidden', 'saldoIcon');
setupToggleVisibility('toggleInvestimentos', 'investimentosValue', 'investimentosHidden', 'investimentosIcon');
setupToggleVisibility('toggleCartao', 'cartaoValue', 'cartaoHidden', 'cartaoIcon');

window.addEventListener('resize', function () {
    if (window.innerWidth >= 992) {
        closeSidebar();
    }
});

document.querySelectorAll('.action-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        this.classList.add('clicked');
        setTimeout(() => this.classList.remove('clicked'), 200);
    });
});

document.querySelectorAll('.transaction-item').forEach(item => {
    item.addEventListener('mouseenter', function () {
        this.style.transform = 'translateX(5px)';
    });

    item.addEventListener('mouseleave', function () {
        this.style.transform = 'translateX(0)';
    });
});

function formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

window.addEventListener('DOMContentLoaded', inicializarPagina)

window.addEventListener('DOMContentLoaded', async () => {
    await loadBalanceAndAgencyNumber();
    await loadClientInformation();


    const refreshBtn = document.getElementById('refreshSaldo');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', async () => {
            refreshBtn.disabled = true;
            refreshBtn.classList.add('opacity-50');
            try {
                await loadBalanceAndAgencyNumber();
                await loadClientInformation();
            } finally {
                refreshBtn.disabled = false;
                refreshBtn.classList.remove('opacity-50');
            }
        });
    }
});

document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth'
            });
        }
    });
});
