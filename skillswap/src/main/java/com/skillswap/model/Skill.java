package com.skillswap.model;

public class Skill {

    private Integer idSkill;
    private String name;

    // Construtor vazio
    public Skill() {}

    // Construtor completo
    public Skill(Integer idSkill, String name) {
        this.idSkill = idSkill;
        this.name = name;
    }

    // Getters e Setters
    public Integer getIdSkill() {
        return idSkill;
    }

    public void setIdSkill(Integer idSkill) {
        this.idSkill = idSkill;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "idSkill=" + idSkill +
                ", name='" + name + '\'' +
                '}';
    }
}