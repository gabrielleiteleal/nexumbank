const API_BASE_URL = 'http://localhost:8080';

function applyCPFMask(cpf) {
    return cpf.replace(/\D/g, '')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})/, '$1-$2')
        .replace(/(-\d{2})\d+?$/, '$1');
}

//TODO make function to save Client data

async function userLogin(cpf_cnpj, senha) {

    const response = await fetch(`${API_BASE_URL}/usuario/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            cpf_cnpj: cpf_cnpj,
            senha: senha
        })
    });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Erro ao fazer login');
    }

    return await response.json();
}

//back here
function saveUserData(dadosUsuario) {
    sessionStorage.setItem('usuario', JSON.stringify(dadosUsuario));
    sessionStorage.setItem('isLoggedIn', 'true');
}

function showError(mensagem) {
    const modalHTML = `
        <div class="modal fade" id="erroModal" tabindex="-1" aria-labelledby="erroModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="erroModalLabel">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>Erro no Login
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body text-center py-4">
                        <i class="bi bi-x-circle text-danger" style="font-size: 4rem;"></i>
                        <h4 class="mt-3">Acesso Negado</h4>
                        <p class="text-muted">${mensagem}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    const modalAnterior = document.getElementById('erroModal');
    if (modalAnterior) {
        modalAnterior.remove();
    }

    document.body.insertAdjacentHTML('beforeend', modalHTML);
    const modal = new bootstrap.Modal(document.getElementById('erroModal'));
    modal.show();
}

function showLoading(botao) {
    const textoOriginal = botao.innerHTML;
    botao.disabled = true;
    botao.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Entrando...';
    return textoOriginal;
}

function restoreButton(botao, textoOriginal) {
    botao.disabled = false;
    botao.innerHTML = textoOriginal;
}

function togglePasswordVisibility() {
    const togglePassword = document.getElementById('togglePassword');
    const senhaInput = document.getElementById('senha');
    const toggleIcon = document.getElementById('toggleIcon');

    if (togglePassword && senhaInput && toggleIcon) {
        togglePassword.addEventListener('click', function () {
            if (senhaInput.type === 'password') {
                senhaInput.type = 'text';
                toggleIcon.classList.remove('bi-eye');
                toggleIcon.classList.add('bi-eye-slash');
            } else {
                senhaInput.type = 'password';
                toggleIcon.classList.remove('bi-eye-slash');
                toggleIcon.classList.add('bi-eye');
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const cpfInput = document.getElementById('cpf');
    if (cpfInput) {
        cpfInput.addEventListener('input', function (e) {
            e.target.value = applyCPFMask(e.target.value);
        });
    }

    togglePasswordVisibility();
});


(function () {
    'use strict';
    window.addEventListener('load', function () {
        const form = document.querySelector('form');

        if (form) {
            form.addEventListener('submit', async function (event) {
                event.preventDefault();
                event.stopPropagation();

                const cpfInput = document.getElementById('cpf');
                const senhaInput = document.getElementById('senha');
                const submitBtn = form.querySelector('button[type="submit"]');

                if (!cpfInput.value || !senhaInput.value) {
                    form.classList.add('was-validated');
                    return;
                }

                const textoOriginal = showLoading(submitBtn);

                const cpf_cnpj = cpfInput.value;
                const senha = senhaInput.value;

                try {
                    const resultado = await userLogin(cpf_cnpj, senha);

                    saveUserData(resultado);

                    window.location.href = 'conta.html';
                } catch (error) {
                    console.error('Erro ao fazer login:', error);
                    showError(error.message);
                    restoreButton(submitBtn, textoOriginal);
                }

                form.classList.add('was-validated');
            }, false);
        }
    }, false);
})();

