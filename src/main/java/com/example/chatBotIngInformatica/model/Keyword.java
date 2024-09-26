package com.example.chatBotIngInformatica.model;

import java.util.List;

public class Keyword {

    private String keyword;
    private List<String> sinonimos;
    private String answer;

    // Getters y setters

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getSinonimos() {
        return sinonimos;
    }

    public void setSinonimos(List<String> sinonimos) {
        this.sinonimos = sinonimos;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
