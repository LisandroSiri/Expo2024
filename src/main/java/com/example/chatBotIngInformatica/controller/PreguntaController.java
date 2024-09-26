package com.example.chatBotIngInformatica.controller;

import com.example.chatBotIngInformatica.service.PreguntaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PreguntaController {

    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    @GetMapping("/pregunta")
    public String responderPregunta(@RequestParam String pregunta) {
        return preguntaService.responderPregunta(pregunta);
    }
}
