package com.skillswap.dto;

public class AtualizarPerfilDTO {

    private String nome;
    private String email;
    private String bio;

    public AtualizarPerfilDTO() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}