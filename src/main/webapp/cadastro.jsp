<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MeuAcervo - Criar Conta</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cadastro.css">
</head>
<body>
<div class="page">
    <section class="hero">
        <div class="brand">
            <span class="brand-mark" aria-hidden="true"></span>
            <span>MeuAcervo</span>
        </div>

        <div class="hero-copy">
            <h1>Comece a montar <span>seu acervo</span></h1>
            <p>Registre seus discos, avalie albuns e acompanhe sua evolucao musical.</p>

            <div class="hero-points">
                <div class="point">
                    <span class="point-icon">&#9733;</span>
                    <div>
                        <strong>Avalie e descubra</strong>
                        Estrelas, reviews e o que a galera esta ouvindo agora.
                    </div>
                </div>
                <div class="point">
                    <span class="point-icon">&#9829;</span>
                    <div>
                        <strong>Lista de desejos</strong>
                        Salve os discos que voce quer comprar e nunca mais esqueca.
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="panel">
        <div class="panel-card">
            <div class="eyebrow">BEM-VINDO</div>
            <h2>Crie sua conta</h2>
            <p>E rapido. Em menos de um minuto voce estara pronto para montar seu acervo.</p>

            <c:if test="${param.erro == 'campos-vazios'}">
                <div class="alert error">Preencha todos os campos obrigatorios.</div>
            </c:if>
            <c:if test="${param.erro == 'email-invalido'}">
                <div class="alert error">Informe um e-mail valido.</div>
            </c:if>
            <c:if test="${param.erro == 'usuario-existente'}">
                <div class="alert error">Usuario ja cadastrado.</div>
            </c:if>

            <form method="post" action="<%= request.getContextPath() %>/cadastro">
                <div class="field">
                    <label for="nome">Nome completo</label>
                    <input id="nome" name="nome" type="text" placeholder="Digite seu nome" required>
                </div>
                <div class="field">
                    <label for="username">Nome de usuario</label>
                    <input id="username" name="username" type="text" placeholder="seunome" required>
                </div>
                <div class="field">
                    <label for="email">E-mail</label>
                    <input id="email" name="email" type="email" placeholder="seu@email.com" required>
                </div>
                <div class="field">
                    <label for="senha">Senha</label>
                    <input id="senha" name="senha" type="password" minlength="6" placeholder="Minimo 6 caracteres" required>
                </div>
                <div class="field">
                    <label for="confirmar">Confirmar senha</label>
                    <input id="confirmar" name="confirmar" type="password" minlength="6" placeholder="Minimo 6 caracteres" required>
                </div>

                <button class="submit" type="submit">Criar minha conta</button>
            </form>

            <div class="form-footer">
                Ja tem conta? <a href="<%= request.getContextPath() %>/login.jsp">Entrar</a>
            </div>
        </div>
    </section>
</div>
</body>
</html>
