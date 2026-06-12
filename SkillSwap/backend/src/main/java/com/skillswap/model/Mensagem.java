package com.skillswap.model;

import java.time.LocalDateTime;

public class Mensagem {

    private Integer idMensagem;
    private Integer tipo;
    private String conteudo;
    private LocalDateTime dataHora;
    private Integer remetente;
    private Integer amizade;

    // Construtor vazio
    public Mensagem() {}

    // Construtor completo
    public Mensagem(Integer idMensagem,
                    Integer tipo,
                    String conteudo,
                    LocalDateTime dataHora,
                    Integer remetente,
                    Integer amizade) {

        this.idMensagem = idMensagem;
        this.tipo = tipo;
        this.conteudo = conteudo;
        this.dataHora = dataHora;
        this.remetente = remetente;
        this.amizade = amizade;
    }

    // Getters e Setters
    public Integer getIdMensagem() {
        return idMensagem;
    }

    public void setIdMensagem(Integer idMensagem) {
        this.idMensagem = idMensagem;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getRemetente() {
        return remetente;
    }

    public void setRemetente(Integer remetente) {
        this.remetente = remetente;
    }

    public Integer getAmizade() {
        return amizade;
    }

    public void setAmizade(Integer amizade) {
        this.amizade = amizade;
    }

    @Override
    public String toString() {
        return "Mensagem{" +
                "idMensagem=" + idMensagem +
                ", tipo=" + tipo +
                ", conteudo='" + conteudo + '\'' +
                ", dataHora=" + dataHora +
                ", remetente=" + remetente +
                ", amizade=" + amizade +
                '}';
    }
}