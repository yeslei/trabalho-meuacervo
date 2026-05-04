package com.seusite.discos.controller;

import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Página inicial (Home/Feed). Mapeada como "/listarFeedServlet" para casar
 * com os href do header.jsp e o redirect do LoginServlet.
 *
 * NÃO exige login: visitantes também conseguem ver a home.
 */
@WebServlet("/listarFeedServlet")
public class ListarFeedServlet extends HttpServlet {

    private final DiscoDAO discoDAO = new DiscoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");

        try {
            List<Disco> discos = discoDAO.listarRecentes(24);
            request.setAttribute("discos", discos);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("discos", Collections.emptyList());
            request.setAttribute("mensagemErro", "Não foi possível carregar o feed.");
        }

        request.setAttribute("usuarioLogado", usuario);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
