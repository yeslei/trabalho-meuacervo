package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AuthService;
import com.seusite.discos.util.JsonUtil;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * GET /api/me — "quem sou eu".
 *
 * Existe porque o cookie de sessao e HttpOnly: o React NAO consegue le-lo.
 * Ao abrir o app, o front chama este endpoint para descobrir se a sessao
 * ainda e valida e reidratar o estado de login (AuthContext).
 *
 * Fora do AuthFilter de proposito: aqui um 401 e uma resposta NORMAL
 * (significa "ninguem logado"), nao um erro a ser tratado.
 */
@WebServlet("/api/me")
public class MeServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    public static class AtualizarPerfilRequest {
        public String bio;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session == null) ? null : (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            JsonUtil.erro(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "nao-autenticado", "Nenhuma sessao ativa.");
            return;
        }
        JsonUtil.ok(response, LoginServlet.montarUsuarioPublico(usuario));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session == null) ? null : (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            JsonUtil.erro(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "nao-autenticado", "Nenhuma sessao ativa.");
            return;
        }

        AtualizarPerfilRequest dados = JsonUtil.lerCorpo(request, AtualizarPerfilRequest.class);
        if (dados == null) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "dados-ausentes", "Informe os dados do perfil.");
            return;
        }

        try {
            Usuario atualizado = authService.atualizarBio(usuario, dados.bio);
            session.setAttribute("usuarioLogado", atualizado);
            JsonUtil.ok(response, LoginServlet.montarUsuarioPublico(atualizado));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "bio-longa", "A bio deve ter no maximo 280 caracteres.");
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "banco", "Erro ao atualizar o perfil.");
        }
    }
}
