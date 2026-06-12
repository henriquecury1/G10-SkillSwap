package com.skillswap.model;

public class Qualificacao {

    private Integer qualificado;
    private Integer skill;

    // Construtor vazio
    public Qualificacao() {}

    // Construtor completo
    public Qualificacao(Integer qualificado,
                        Integer skill) {

        this.qualificado = qualificado;
        this.skill = skill;
    }

    // Getters e Setters
    public Integer getQualificado() {
        return qualificado;
    }

    public void setQualificado(Integer qualificado) {
        this.qualificado = qualificado;
    }

    public Integer getSkill() {
        return skill;
    }

    public void setSkill(Integer skill) {
        this.skill = skill;
    }

    @Override
    public String toString() {
        return "Qualificacao{" +
                "qualificado=" + qualificado +
                ", skill=" + skill +
                '}';
    }
}