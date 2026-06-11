package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AuthService;
import com.seusite.discos.util.JsonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * POST /login  — autentica e cria a sessao.
 *
 * Parte 2: em vez de redirecionar (PRG), recebe JSON no corpo e responde JSON.
 * A AUTENTICACAO CONTINUA POR SESSAO (opcao 2): o login cria a HttpSession e,
 * se "lembrar" vier true, grava os cookies de 7 dias — exatamente como na Parte 1.
 * O front mantem o cookie de sessao automaticamente (fetch com credentials).
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    /** Formato do corpo JSON esperado no login. */
    public static class LoginRequest {
        public String email;
        public String senha;
        public boolean lembrar;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        LoginRequest dados = JsonUtil.lerCorpo(request, LoginRequest.class);
        if (dados == null || dados.email == null || dados.senha == null) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "campos-vazios", "Informe email e senha.");
            return;
        }

        try {
            Usuario usuario = authService.autenticarUsuario(dados.email, dados.senha);

            if (usuario == null) {
                JsonUtil.erro(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "login-invalido", "E-mail ou senha incorretos.");
                return;
            }

            // Cria a sessao (mesma logica da Parte 1)
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogado", usuario);
            session.setMaxInactiveInterval(60 * 30);

            // "Manter conectado": cookies de 7 dias (reaproveitado da Parte 1)
            if (dados.lembrar) {
                String path = request.getContextPath().isEmpty() ? "/" : request.getContextPath();

                Cookie cookieEmail = new Cookie("usuarioEmail", usuario.getEmail());
                cookieEmail.setMaxAge(60 * 60 * 24 * 7);
                cookieEmail.setPath(path);
                cookieEmail.setHttpOnly(true);
                response.addCookie(cookieEmail);

                Cookie cookieId = new Cookie("usuarioId", String.valueOf(usuario.getIdUsuario()));
                cookieId.setMaxAge(60 * 60 * 24 * 7);
                cookieId.setPath(path);
                cookieId.setHttpOnly(true);
                response.addCookie(cookieId);
            }

            JsonUtil.ok(response, montarUsuarioPublico(usuario));

        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "banco", "Erro ao acessar o banco de dados.");
        }
    }

    /** Devolve apenas os campos publicos do usuario (NUNCA a senha). */
    static Map<String, Object> montarUsuarioPublico(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idUsuario", u.getIdUsuario());
        m.put("nome", u.getNome());
        m.put("email", u.getEmail());
        m.put("username", u.getUsername());
        return m;
    }
}
