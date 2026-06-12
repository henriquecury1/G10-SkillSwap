package com.skillswap.model;

import java.time.LocalDateTime;

public class Avaliacao {

    private String idAvaliacao;
    private Integer avaliado;
    private Integer avaliador;
    private Float nota;
    private LocalDateTime dataHora;

    // Construtor vazio
    public Avaliacao() {}

    // Construtor completo
    public Avaliacao(String idAvaliacao,
                      Integer avaliado,
                      Integer avaliador,
                      Float nota,
                      LocalDateTime dataHora) {

        this.idAvaliacao = idAvaliacao;
        this.avaliado = avaliado;
        this.avaliador = avaliador;
        this.nota = nota;
        this.dataHora = dataHora;
    }

    // Getters e Setters
    public String getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(String idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public Integer getAvaliado() {
        return avaliado;
    }

    public void setAvaliado(Integer avaliado) {
        this.avaliado = avaliado;
    }

    public Integer getAvaliador() {
        return avaliador;
    }

    public void setAvaliador(Integer avaliador) {
        this.avaliador = avaliador;
    }

    public Float getNota() {
        return nota;
    }

    public void setNota(Float nota) {
        this.nota = nota;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "Avaliacao{" +
                "idAvaliacao='" + idAvaliacao + '\'' +
                ", avaliado=" + avaliado +
                ", avaliador=" + avaliador +
                ", nota=" + nota +
                ", dataHora=" + dataHora +
                '}';
    }
}