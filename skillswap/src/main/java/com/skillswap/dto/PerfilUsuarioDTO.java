package com.skillswap.dto;

import com.skillswap.model.Skill;
import com.skillswap.model.Usuario;

import java.util.List;

public class PerfilUsuarioDTO {

    private Usuario usuario;
    private List<Skill> skills;

    public PerfilUsuarioDTO() {}

    public PerfilUsuarioDTO(Usuario usuario, List<Skill> skills) {
        this.usuario = usuario;
        this.skills = skills;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
}