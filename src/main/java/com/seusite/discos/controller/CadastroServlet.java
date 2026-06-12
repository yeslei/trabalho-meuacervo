package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AuthService;
import com.seusite.discos.util.JsonUtil;
import com.seusite.discos.util.ValidadorUtil;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * POST /cadastro — cria a conta e ja faz login automatico (cria a sessao).
 * Recebe e responde JSON. As validacoes e o AuthService sao os da Parte 1.
 */
@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    public static class CadastroRequest {
        public String nome;
        public String email;
        public String senha;
        public String username;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        CadastroRequest dados = JsonUtil.lerCorpo(request, CadastroRequest.class);
        if (dados == null) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "campos-vazios", "Dados de cadastro ausentes.");
            return;
        }

        if (ValidadorUtil.campoVazio(dados.nome) || ValidadorUtil.campoVazio(dados.email)
                || ValidadorUtil.campoVazio(dados.senha)) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "campos-vazios", "Preencha nome, e-mail e senha.");
            return;
        }
        if (!ValidadorUtil.emailValido(dados.email)) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "email-invalido", "E-mail em formato invalido.");
            return;
        }
        if (dados.senha.length() < 8) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "senha-curta", "A senha deve ter ao menos 8 caracteres.");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome.trim());
        usuario.setEmail(dados.email.trim());
        usuario.setSenha(dados.senha);
        usuario.setUsername(dados.username == null ? null : dados.username.trim());

        try {
            authService.cadastrarUsuario(usuario);
            Usuario criado = authService.autenticarUsuario(dados.email.trim(), dados.senha);

            if (criado != null) {
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogado", criado);
                session.setMaxInactiveInterval(60 * 30);
                JsonUtil.escreverJson(response, HttpServletResponse.SC_CREATED,
                        LoginServlet.montarUsuarioPublico(criado, session));
            } else {
                JsonUtil.sucesso(response, "Cadastro realizado. Faca login.");
            }

        } catch (IllegalArgumentException e) {
            String codigo = e.getMessage() == null ? "validacao" : e.getMessage();
            JsonUtil.erro(response, HttpServletResponse.SC_CONFLICT, codigo, mensagemPara(codigo));
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "banco", "Erro ao acessar o banco de dados.");
        }
    }

    private String mensagemPara(String codigo) {
        return switch (codigo) {
            case "email-existente" -> "Este e-mail ja esta cadastrado.";
            case "username-existente" -> "Este nome de usuario ja esta em uso.";
            default -> "Nao foi possivel concluir o cadastro.";
        };
    }
}
