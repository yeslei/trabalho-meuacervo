package com.seusite.discos.service;

import com.seusite.discos.dao.CurtidaDAO;
import com.seusite.discos.dao.PostDAO;

import java.sql.SQLException;

public class CurtidaService {

    private final CurtidaDAO curtidaDAO = new CurtidaDAO();
    private final PostDAO postDAO = new PostDAO();

    public boolean alternarCurtida(int idUsuario, int idPost) throws SQLException {
        if (!postDAO.existePorId(idPost)) {
            throw new IllegalArgumentException("post-inexistente");
        }
        if (curtidaDAO.existe(idUsuario, idPost)) {
            curtidaDAO.remover(idUsuario, idPost);
            return false;
        }
        curtidaDAO.inserir(idUsuario, idPost);
        return true;
    }

    public int contarPorPost(int idPost) throws SQLException {
        return curtidaDAO.contarPorPost(idPost);
    }

    public boolean usuarioCurtiu(int idUsuario, int idPost) throws SQLException {
        return curtidaDAO.existe(idUsuario, idPost);
    }
}
