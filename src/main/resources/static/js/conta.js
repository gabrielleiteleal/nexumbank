const API_BASE_URL = 'http://localhost:8080';

const sidebarToggle = document.getElementById('sidebarToggle');
const sidebar = document.querySelector('.sidebar');
const overlay = document.getElementById('sidebarOverlay');

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

    try {
        const response = await fetch(`${API_BASE_URL}/conta`, {
            method: 'GET'
        });

        if (!response.ok) {
            let errorMsg = "Erro ao buscar a conta";
            try {
                const errorData = await response.json();
                errorMsg = errorData?.message || errorMsg;
            } catch (e) {
            }
            throw new Error(errorMsg);
        }

        const data = await response.json();

        let balance = null;
        if (Array.isArray(data) && data.length > 0) {
            balance = data[0]?.saldo ?? null;
        } else if (data && typeof data === 'object') {
            balance = data.saldo ?? null;
        }

        if (balance === null || balance === undefined) {
            throw new Error('Saldo não encontrado no retorno da API');
        }

        console.log(balance);
        return balance;

    } catch (erro) {
        throw erro;
    }

}

async function loadAndRenderBalance() {
    const balanceElement = document.getElementById('saldoValue');
    if (!balanceElement) return;

    try {
        const balance = await setUserBalance();
        balanceElement.textContent = formatCurrency(balance);
    } catch (error) {
        console.error('Erro ao carregar saldo:', error);
        balanceElement.textContent = 'R$ 0,00';
    }
}

async function setAgencyNumber() {

    try {
        const response = await fetch(`${API_BASE_URL}/conta`, {
            method: 'GET'
        })

        if (!response.ok) {
            let errorMsg = "Erro ao buscar a conta";
            try {
                const errorData = await response.json();
                errorMsg = errorData?.message || errorMsg;
            } catch (e) {
            }
            throw new Error(errorMsg);
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

async function loadAndRenderAgencyNumber() {
    const agencyNumberElement = document.getElementById('agencyNumber');
    if (!agencyNumberElement) return;

    try {
        agencyNumberElement.textContent = await setAgencyNumber();
    } catch (error) {
        console.error('Erro ao carregar agência:', error);
        agencyNumberElement.textContent = '000-0';
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

window.addEventListener('DOMContentLoaded', () => {
    loadAndRenderBalance();
    loadAndRenderAgencyNumber()

    const refreshBtn = document.getElementById('refreshSaldo');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', async () => {
            refreshBtn.disabled = true;
            refreshBtn.classList.add('opacity-50');
            try {
                await loadAndRenderBalance();
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