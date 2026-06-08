package com.skillswap.model;

public class Amizade {

    private Integer idAmizade;
    private Integer status;
    private Integer usuario1;
    private Integer usuario2;

    // Construtor vazio
    public Amizade() {}

    // Construtor completo
    public Amizade(Integer idAmizade,
                   Integer status,
                   Integer usuario1,
                   Integer usuario2) {

        this.idAmizade = idAmizade;
        this.status = status;
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
    }

    // Getters e Setters
    public Integer getIdAmizade() {
        return idAmizade;
    }

    public void setIdAmizade(Integer idAmizade) {
        this.idAmizade = idAmizade;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(Integer usuario1) {
        this.usuario1 = usuario1;
    }

    public Integer getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(Integer usuario2) {
        this.usuario2 = usuario2;
    }

    @Override
    public String toString() {
        return "Amizade{" +
                "idAmizade=" + idAmizade +
                ", status=" + status +
                ", usuario1=" + usuario1 +
                ", usuario2=" + usuario2 +
                '}';
    }
}