package com.skillswap.dto;

public class AvaliacaoDTO {

    private Integer idAvaliado;
    private Float nota;

    public AvaliacaoDTO() {}

    public Integer getIdAvaliado() {
        return idAvaliado;
    }

    public void setIdAvaliado(Integer idAvaliado) {
        this.idAvaliado = idAvaliado;
    }

    public Float getNota() {
        return nota;
    }

    public void setNota(Float nota) {
        this.nota = nota;
    }
}