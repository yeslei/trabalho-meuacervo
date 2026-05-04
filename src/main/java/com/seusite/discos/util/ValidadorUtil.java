package com.seusite.discos.util;

public class ValidadorUtil {

    // Valida se um campo está vazio
    public static boolean campoVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    // Valida se o email tem formato correto
    public static boolean emailValido(String email) {
        return email != null && email.matches("^[\\w-]+(?:\\.[\\w-]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7}$");
    }

    // Valida CPF 
    public static boolean cpfValido(String cpf) {
        return cpf != null && cpf.matches("\\d{11}");
    }
}