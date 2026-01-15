// API Configuration
const API_BASE_URL = 'http://localhost:8080';

// Máscaras para campos
function applyCPFMask(cpf) {
    return cpf.replace(/\D/g, '')
             .replace(/(\d{3})(\d)/, '$1.$2')
             .replace(/(\d{3})(\d)/, '$1.$2')
             .replace(/(\d{3})(\d{1,2})/, '$1-$2')
             .replace(/(-\d{2})\d+?$/, '$1');
}

function applyCEPMask(cep) {
    return cep.replace(/\D/g, '')
             .replace(/(\d{5})(\d)/, '$1-$2')
             .replace(/(-\d{3})\d+?$/, '$1');
}

function applyPhoneMask(phone) {
    return phone.replace(/\D/g, '')
               .replace(/(\d{2})(\d)/, '($1) $2')
               .replace(/(\d{4})(\d)/, '$1-$2')
               .replace(/(\d{4})-(\d)(\d{4})/, '$1$2-$3')
               .replace(/(-\d{4})\d+?$/, '$1');
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('cpf').addEventListener('input', function(e) {
        e.target.value = applyCPFMask(e.target.value);
    });

    document.getElementById('cep').addEventListener('input', function(e) {
        e.target.value = applyCEPMask(e.target.value);
    });

    document.getElementById('telefone').addEventListener('input', function(e) {
        e.target.value = applyPhoneMask(e.target.value);
    });

    document.getElementById('toggleSenha').addEventListener('click', function() {
        const senhaInput = document.getElementById('senha');
        const toggleIcon = document.getElementById('toggleSenhaIcon');

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

    document.getElementById('toggleConfirmarSenha').addEventListener('click', function() {
        const senhaInput = document.getElementById('confirmarSenha');
        const toggleIcon = document.getElementById('toggleConfirmarSenhaIcon');

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

    document.getElementById('cep').addEventListener('blur', function() {
        const cep = this.value.replace(/\D/g, '');
        if (cep.length === 8) {
            fetch(`https://viacep.com.br/ws/${cep}/json/`)
                .then(response => response.json())
                .then(data => {
                    if (!data.erro) {
                        document.getElementById('logradouro').value = data.logradouro || '';
                        document.getElementById('bairro').value = data.bairro || '';
                        document.getElementById('cidade').value = data.localidade || '';

                        const estadoSelect = document.getElementById('estado');
                        estadoSelect.value = data.uf || '';
                    }
                })
                .catch(error => console.error('Erro ao buscar CEP:', error));
        }
    });

    document.getElementById('confirmarEmail').addEventListener('input', function() {
        const email = document.getElementById('email').value;
        const confirmarEmail = this.value;

        if (email !== confirmarEmail && confirmarEmail !== '') {
            this.setCustomValidity('Os e-mails devem ser iguais');
        } else {
            this.setCustomValidity('');
        }
    });

    document.getElementById('confirmarSenha').addEventListener('input', function() {
        const senha = document.getElementById('senha').value;
        const confirmarSenha = this.value;

        if (senha !== confirmarSenha && confirmarSenha !== '') {
            this.setCustomValidity('As senhas devem ser iguais');
        } else {
            this.setCustomValidity('');
        }
    });
});

function montarEnderecoCompleto() {
    const logradouro = document.getElementById('logradouro').value;
    const numero = document.getElementById('numero').value;
    const complemento = document.getElementById('complemento').value;
    const bairro = document.getElementById('bairro').value;
    const cidade = document.getElementById('cidade').value;
    const cep = document.getElementById('cep').value;

    let endereco = `${logradouro}, ${numero}`;
    if (complemento) {
        endereco += ` - ${complemento}`;
    }
    endereco += ` - ${bairro}, ${cidade} - CEP: ${cep}`;

    return endereco;
}

function converterDataParaBR(dataISO) {
    if (!dataISO) return null;
    const [ano, mes, dia] = dataISO.split('-');
    return `${dia}/${mes}/${ano}`;
}

async function criarUsuario(dados) {
    try {
        const response = await fetch(`${API_BASE_URL}/usuario`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao criar usuário');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

function mostrarMensagemSucesso() {
    const modalHTML = `
        <div class="modal fade" id="sucessoModal" tabindex="-1" aria-labelledby="sucessoModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title" id="sucessoModalLabel">
                            <i class="bi bi-check-circle-fill me-2"></i>Cadastro Realizado!
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body text-center py-4">
                        <i class="bi bi-check-circle text-success" style="font-size: 4rem;"></i>
                        <h4 class="mt-3">Conta criada com sucesso!</h4>
                        <p class="text-muted">Você será redirecionado para a página de login em alguns segundos...</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success" onclick="window.location.href='index.html'">
                            Ir para Login
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHTML);
    const modal = new bootstrap.Modal(document.getElementById('sucessoModal'));
    modal.show();

    setTimeout(() => {
        window.location.href = 'index.html';
    }, 3000);
}

function mostrarMensagemErro(mensagem) {
    const modalHTML = `
        <div class="modal fade" id="erroModal" tabindex="-1" aria-labelledby="erroModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="erroModalLabel">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>Erro no Cadastro
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body text-center py-4">
                        <i class="bi bi-x-circle text-danger" style="font-size: 4rem;"></i>
                        <h4 class="mt-3">Ops! Algo deu errado</h4>
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

(function() {
    'use strict';
    window.addEventListener('load', function() {
        const forms = document.getElementsByClassName('needs-validation');

        Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('submit', async function(event) {
                event.preventDefault();
                event.stopPropagation();

                const email = document.getElementById('email').value;
                const confirmarEmail = document.getElementById('confirmarEmail').value;
                const confirmarEmailInput = document.getElementById('confirmarEmail');

                if (email !== confirmarEmail) {
                    confirmarEmailInput.setCustomValidity('Os e-mails devem ser iguais');
                } else {
                    confirmarEmailInput.setCustomValidity('');
                }

                const senha = document.getElementById('senha').value;
                const confirmarSenha = document.getElementById('confirmarSenha').value;
                const confirmarSenhaInput = document.getElementById('confirmarSenha');

                if (senha !== confirmarSenha) {
                    confirmarSenhaInput.setCustomValidity('As senhas devem ser iguais');
                } else {
                    confirmarSenhaInput.setCustomValidity('');
                }

                if (form.checkValidity() === true) {
                    const submitBtn = form.querySelector('button[type="submit"]');
                    const btnTextoOriginal = submitBtn.innerHTML;
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Criando conta...';

                    const dados = {
                        nome: document.getElementById('nomeCompleto').value,
                        cpf_cnpj: document.getElementById('cpf').value,
                        email: document.getElementById('email').value,
                        senha: document.getElementById('senha').value,
                        telefone: document.getElementById('telefone').value,
                        endereco: montarEnderecoCompleto(),
                        estado: document.getElementById('estado').value,
                        tipo_usuario: 'CLIENTE',
                        data_nascimento: converterDataParaBR(document.getElementById('dataNascimento').value),
                        profissao: null,
                        renda_mensal: null
                    };

                    console.log('Dados que serão enviados:', dados);

                    try {
                        const resultado = await criarUsuario(dados);
                        console.log('Usuário criado com sucesso:', resultado);
                        mostrarMensagemSucesso();
                    } catch (error) {
                        console.error('Erro ao criar usuário:', error);
                        mostrarMensagemErro(error.message);

                        submitBtn.disabled = false;
                        submitBtn.innerHTML = btnTextoOriginal;
                    }
                }

                form.classList.add('was-validated');
            }, false);
        });
    }, false);
})();
