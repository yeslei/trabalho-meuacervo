package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.PostService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/criar-post")
public class CriarPostServlet extends HttpServlet {

    private final PostService postService = new PostService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        String titulo = request.getParameter("titulo");
        String conteudo = request.getParameter("conteudo");
        String idDiscoStr = request.getParameter("id_disco");
        String voltarSeguro = normalizarVoltar(request.getParameter("voltar"));

        int idDisco;
        try {
            idDisco = Integer.parseInt(idDiscoStr.trim());
        } catch (Exception e) {
            if (voltarSeguro != null) {
                response.sendRedirect(request.getContextPath() + comParametro(voltarSeguro, "erro", "id-disco-invalido"));
            } else {
                response.sendRedirect(request.getContextPath() + "/novoPost.jsp?erro=id-disco-invalido");
            }
            return;
        }

        try {
            postService.criarPost(usuario.getIdUsuario(), idDisco, titulo, conteudo);
            if (voltarSeguro != null) {
                response.sendRedirect(request.getContextPath() + comParametro(voltarSeguro, "sucesso", "post-criado"));
            } else {
                response.sendRedirect(request.getContextPath() + "/feed?sucesso=post-criado");
            }
        } catch (IllegalArgumentException e) {
            String codigo = e.getMessage() == null ? "validacao" : e.getMessage();
            if (voltarSeguro != null) {
                response.sendRedirect(request.getContextPath() + comParametro(voltarSeguro, "erro", codigo));
            } else {
                response.sendRedirect(request.getContextPath() + "/novoPost.jsp?erro=" + codigo + "&id_disco=" + idDisco);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (voltarSeguro != null) {
                response.sendRedirect(request.getContextPath() + comParametro(voltarSeguro, "erro", "banco"));
            } else {
                response.sendRedirect(request.getContextPath() + "/novoPost.jsp?erro=banco&id_disco=" + idDisco);
            }
        }
    }

    private static String normalizarVoltar(String voltar) {
        if (voltar == null) {
            return null;
        }
        String v = voltar.trim();
        if (v.isEmpty()) {
            return null;
        }
        if (!v.startsWith("/") || v.startsWith("//") || v.contains("\\") || v.contains("\r") || v.contains("\n")) {
            return null;
        }
        return v;
    }

    private static String comParametro(String path, String chave, String valor) {
        String sep = path.contains("?") ? "&" : "?";
        return path + sep + chave + "=" + URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}
