package com.skillswap.dto;

import com.skillswap.model.Skill;
import com.skillswap.model.Usuario;

import java.util.List;

public class MatchResultDTO {
    private Usuario usuario;
    private List<Skill> skills;
    private int score;
    private String reason;
    private List<String> compatibleSkills;
    private List<String> scheduleOverlap;

    public MatchResultDTO() {}

    public MatchResultDTO(Usuario usuario, List<Skill> skills, int score, String reason,
                          List<String> compatibleSkills, List<String> scheduleOverlap) {
        this.usuario = usuario;
        this.skills = skills;
        this.score = score;
        this.reason = reason;
        this.compatibleSkills = compatibleSkills;
        this.scheduleOverlap = scheduleOverlap;
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public List<String> getCompatibleSkills() { return compatibleSkills; }
    public void setCompatibleSkills(List<String> compatibleSkills) { this.compatibleSkills = compatibleSkills; }
    public List<String> getScheduleOverlap() { return scheduleOverlap; }
    public void setScheduleOverlap(List<String> scheduleOverlap) { this.scheduleOverlap = scheduleOverlap; }
}
