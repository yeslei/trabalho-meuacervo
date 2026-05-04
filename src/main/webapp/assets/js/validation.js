'use strict';

document.addEventListener('DOMContentLoaded', function () {

    // ── Show/hide de senha ────────────────────────────────────────────────
    document.querySelectorAll('.btn-toggle-senha').forEach(function (btn) {
        btn.addEventListener('click', function () {
            const input = btn.previousElementSibling;
            if (!input) return;
            const escondido = input.type === 'password';
            input.type = escondido ? 'text' : 'password';
            const icon = btn.querySelector('i');
            if (icon) {
                icon.className = escondido ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye';
            }
        });
    });

    // ── Contador de caracteres para username ──────────────────────────────
    const usernameInput = document.getElementById('username');
    const usernameCounter = document.getElementById('username-counter');
    if (usernameInput && usernameCounter) {
        usernameInput.addEventListener('input', function () {
            const len = usernameInput.value.length;
            usernameCounter.textContent = len + '/15';
            usernameCounter.style.color = len >= 15
                ? 'var(--danger)'
                : 'var(--secondary-text)';
        });

        usernameInput.addEventListener('blur', function () {
            limparErroCampo(usernameInput);
            if (usernameInput.value && !validarUsername(usernameInput.value)) {
                mostrarErro(usernameInput, 'Use letras, números e _ (3 a 15 caracteres).');
            }
        });
    }

    // ── Validação do formulário de login ──────────────────────────────────
    const loginForm = document.getElementById('form-login');
    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
            limparErros(loginForm);
            let valido = true;

            const email = document.getElementById('email');
            const senha = document.getElementById('senha');

            if (!validarEmail(email.value)) {
                mostrarErro(email, 'Informe um e-mail válido.');
                valido = false;
            }
            if (senha.value.trim() === '') {
                mostrarErro(senha, 'A senha não pode estar vazia.');
                valido = false;
            }

            if (!valido) e.preventDefault();
        });
    }

    // ── Validação do formulário de cadastro ───────────────────────────────
    const cadastroForm = document.getElementById('form-cadastro');
    if (cadastroForm) {
        cadastroForm.addEventListener('submit', function (e) {
            limparErros(cadastroForm);
            let valido = true;

            const nome    = document.getElementById('nome');
            const email   = document.getElementById('email');
            const senha   = document.getElementById('senha');

            if (!nome || nome.value.trim().length < 2) {
                mostrarErro(nome, 'Informe seu nome completo.');
                valido = false;
            }
            if (usernameInput && !validarUsername(usernameInput.value)) {
                mostrarErro(usernameInput, 'Use letras, números e _ (3 a 15 caracteres).');
                valido = false;
            }
            if (!validarEmail(email.value)) {
                mostrarErro(email, 'Informe um e-mail válido.');
                valido = false;
            }
            if (senha.value.length < 8) {
                mostrarErro(senha, 'A senha precisa ter pelo menos 8 caracteres.');
                valido = false;
            }

            if (!valido) e.preventDefault();
        });
    }

    // ── Animação fade-in para cards de álbum (IntersectionObserver) ───────
    if ('IntersectionObserver' in window) {
        const cards = document.querySelectorAll('.album-card');
        const observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('card-visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.08 });

        cards.forEach(function (card) {
            card.classList.add('card-hidden');
            observer.observe(card);
        });
    }

    // ── Previne duplo envio de formulários ────────────────────────────────
    document.querySelectorAll('form[data-single-submit]').forEach(function (form) {
        form.addEventListener('submit', function () {
            const btn = form.querySelector('[type="submit"]');
            if (btn && !btn.disabled) {
                btn.disabled = true;
                btn.dataset.textoOriginal = btn.textContent;
                btn.textContent = 'Aguarde...';
            }
        });
    });

    // ── Helpers ───────────────────────────────────────────────────────────
    function validarEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test((email || '').trim());
    }

    function validarUsername(username) {
        return /^[a-zA-Z0-9_]{3,15}$/.test((username || '').trim());
    }

    function mostrarErro(input, mensagem) {
        if (!input) return;
        input.classList.add('input-error');
        const span = document.createElement('span');
        span.className = 'field-error';
        span.textContent = mensagem;
        input.insertAdjacentElement('afterend', span);
        input.focus();
    }

    function limparErroCampo(input) {
        if (!input) return;
        input.classList.remove('input-error');
        const span = input.parentNode.querySelector('.field-error');
        if (span) span.remove();
    }

    function limparErros(form) {
        form.querySelectorAll('.field-error').forEach(function (el) { el.remove(); });
        form.querySelectorAll('.input-error').forEach(function (el) {
            el.classList.remove('input-error');
        });
    }
});
