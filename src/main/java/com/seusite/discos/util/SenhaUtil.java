package com.seusite.discos.util;

import org.mindrot.jbcrypt.BCrypt;

public class SenhaUtil {

    // Método para gerar hash
    public static String gerarHash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    // Método para verificar a senha
    public static boolean verificarSenha(String senhaDigitada, String senhaSalva) {
        return BCrypt.checkpw(senhaDigitada, senhaSalva);
    }
}