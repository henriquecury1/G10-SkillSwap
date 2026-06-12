package com.skillswap.dto;

public class AtualizarSenhaDTO {

    private String senhaAtual;
    private String novaSenha;

    public AtualizarSenhaDTO() {}

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}