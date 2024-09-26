package com.example.chatBotIngInformatica.service;

import com.example.chatBotIngInformatica.model.Keyword;
import com.example.chatBotIngInformatica.utils.TextoNormalizador;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PreguntaService {
    private List<Keyword> keywords;
    private TokenizerME tokenizer;
    private final TextoNormalizador textoNormalizador;
    private final LevenshteinDistance levenshteinDistance;

    public PreguntaService(TextoNormalizador textoNormalizador) {
        this.textoNormalizador = textoNormalizador;
        this.levenshteinDistance = new LevenshteinDistance();
    }

    @PostConstruct
    public void init() throws IOException {
        loadKeywords();
        loadTokenizer();
    }

    private void loadKeywords() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Keyword>> typeReference = new TypeReference<>() {};
        InputStream inputStream = new ClassPathResource("keywords.json").getInputStream();
        keywords = mapper.readValue(inputStream, typeReference);
    }

    private void loadTokenizer() throws IOException {
        InputStream modelIn = new ClassPathResource("es-token.bin").getInputStream();
        TokenizerModel model = new TokenizerModel(modelIn);
        tokenizer = new TokenizerME(model);
    }

    public String responderPregunta(String pregunta) {
        String preguntaNormalizada = textoNormalizador.normalizar(pregunta);
        String[] tokens = tokenizer.tokenize(preguntaNormalizada);

        Optional<Keyword> matchedKeyword = keywords.stream()
                .filter(kw -> coincideConKeywordOSinonimo(kw, tokens))
                .findFirst();

        if (matchedKeyword.isPresent()) {
            Keyword kw = matchedKeyword.get();
            String respuesta = kw.getAnswer();
            String palabraEncontrada = encontrarPalabraEnPregunta(kw, preguntaNormalizada);
            return respuesta.replace(kw.getKeyword(), palabraEncontrada);
        }

        return "Lo siento, no tengo información sobre esa pregunta.";
    }

    private boolean coincideConKeywordOSinonimo(Keyword keyword, String[] tokens) {
        String keywordNormalizada = textoNormalizador.normalizar(keyword.getKeyword());
        if (contienepalabra(tokens, keywordNormalizada)) {
            return true;
        }
        return keyword.getSinonimos().stream()
                .map(textoNormalizador::normalizar)
                .anyMatch(sinonimo -> contieneOEsSimilar(tokens, sinonimo));
    }

    private boolean contienepalabra(String[] tokens, String palabra) {
        return java.util.Arrays.stream(tokens)
                .anyMatch(token -> token.equalsIgnoreCase(palabra));
    }

    private boolean contieneOEsSimilar(String[] tokens, String palabra) {
        return contieneSubcadena(tokens, palabra) || esSimilar(tokens, palabra);
    }

    private boolean contieneSubcadena(String[] tokens, String subcadena) {
        return Pattern.compile(Pattern.quote(subcadena), Pattern.CASE_INSENSITIVE)
                .matcher(String.join(" ", tokens)).find();
    }

    private boolean esSimilar(String[] tokens, String palabra) {
        return java.util.Arrays.stream(tokens)
                .anyMatch(token -> levenshteinDistance.apply(token, palabra) <= 2);
    }

    private String encontrarPalabraEnPregunta(Keyword keyword, String pregunta) {
        if (pregunta.toLowerCase().contains(keyword.getKeyword().toLowerCase())) {
            return keyword.getKeyword();
        }
        return keyword.getSinonimos().stream()
                .filter(sinonimo -> pregunta.toLowerCase().contains(sinonimo.toLowerCase()))
                .findFirst()
                .orElse(keyword.getKeyword());
    }
}
