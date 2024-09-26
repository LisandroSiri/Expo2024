package com.example.chatBotIngInformatica.utils;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class TextoNormalizador {

    public String normalizar(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("[^\\p{ASCII}]", "");
        return normalizado.toLowerCase().trim();
    }
}
