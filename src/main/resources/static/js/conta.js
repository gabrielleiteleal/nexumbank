const API_BASE_URL = 'http://localhost:8080';

const sidebarToggle = document.getElementById('sidebarToggle');
const sidebar = document.querySelector('.sidebar');
const overlay = document.getElementById('sidebarOverlay');

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

        console.log('Dados carregados:', {usuario, cliente, conta});

    } catch (error) {
        console.error('Erro ao inicializar página:', error);
        showNotification('Erro ao carregar dados do perfil', 'danger');
    } finally {
        document.body.style.opacity = '1';
    }
}

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

sidebarToggle?.addEventListener('click', toggleSidebar);
overlay?.addEventListener('click', closeSidebar);

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

async function setUserBalance() {
    const usuario = verificarAutenticacao();
    const conta = await carregarDadosConta(usuario.id_usuario);

    return conta.saldo;

}

async function loadBalanceAndAgencyNumber() {
    const balanceElement = document.getElementById('saldoValue');
    const agencyNumberElement = document.getElementById('agencyNumber');
    if (!balanceElement) return;
    if (!agencyNumberElement) return;

    try {
        const balance = await setUserBalance();
        balanceElement.textContent = formatCurrency(balance);
    } catch (error) {
        console.error('Erro ao carregar saldo:', error);
        balanceElement.textContent = 'R$ 0,00';
    }

    try {
        agencyNumberElement.textContent = await setAgencyNumber();
    } catch (error) {
        console.error('Erro ao carregar agência:', error);
        agencyNumberElement.textContent = '000-0';
    }

}

async function loadClientInformation() {
    const usuario = verificarAutenticacao();
    document.getElementById('nome').textContent = usuario.nome;
}

async function setAgencyNumber() {

    try {
        const response = await fetch(`${API_BASE_URL}/conta`, {
            method: 'GET'
        })

        if (!response.ok) {
            let errorMsg = "Erro ao buscar a conta";
            throw new Error(errorMsg);
            // try {
            //     const errorData = await response.json();
            //     errorMsg = errorData?.message || errorMsg;
            // } catch (e) {
            // }
            // throw new Error(errorMsg);
        }

        const data = await response.json();

        let agencyNumber = null;
        if (Array.isArray(data) && data.length > 0) {
            agencyNumber = data[0]?.agencia ?? null;
        } else if (data && typeof data === 'object') {
            agencyNumber = data.agencia ?? null;
        }

        if (agencyNumber === null || agencyNumber === undefined) {
            throw new Error('Agência não encontrada no retorno da API');
        }

        console.log(agencyNumber);
        return agencyNumber

    } catch (erro) {
        throw erro;
    }
}

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

window.addEventListener('DOMContentLoaded', () => {
    loadBalanceAndAgencyNumber();
    loadClientInformation();

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