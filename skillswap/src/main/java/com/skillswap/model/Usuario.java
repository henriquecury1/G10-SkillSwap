package com.skillswap.model;

import java.math.BigDecimal;

public class Usuario {

    private Integer idUsuario;
    private String email;
    private String senha;
    private String bio;
    private String nome;
    private BigDecimal nota;
    private Integer numAvaliacoes;

    // Construtor vazio
    public Usuario() {}

    // Construtor completo
    public Usuario(Integer idUsuario,
                   String email,
                   String senha,
                   String bio,
                   String nome,
                   BigDecimal nota,
                   Integer numAvaliacoes) {

        this.idUsuario = idUsuario;
        this.email = email;
        this.senha = senha;
        this.bio = bio;
        this.nome = nome;
        this.nota = nota;
        this.numAvaliacoes = numAvaliacoes;
    }

    // Getters e Setters

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public Integer getNumAvaliacoes() {
        return numAvaliacoes;
    }

    public void setNumAvaliacoes(Integer numAvaliacoes) {
        this.numAvaliacoes = numAvaliacoes;
    }
}