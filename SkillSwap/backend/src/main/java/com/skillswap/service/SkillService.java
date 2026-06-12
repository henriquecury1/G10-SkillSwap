package com.skillswap.service;

import com.skillswap.dao.SkillDAO;
import com.skillswap.dto.SkillDTO;
import com.skillswap.model.Skill;
import com.skillswap.response.ApiResponse;

import java.util.List;

public class SkillService {

    private final SkillDAO skillDAO;

    public SkillService() {
        this.skillDAO = new SkillDAO();
    }

    public ApiResponse<Void> cadastrar(SkillDTO dto) {
        if (dto == null || isBlank(dto.getName()))
            return ApiResponse.error("Nome da skill obrigatório.");

        if (skillDAO.findByName(dto.getName()) != null)
            return ApiResponse.error("Skill já cadastrada.");

        Skill skill = new Skill();
        skill.setName(dto.getName());

        if (!skillDAO.save(skill))
            return ApiResponse.error("Erro ao cadastrar skill.");

        return ApiResponse.success("Skill cadastrada com sucesso.");
    }

    public ApiResponse<List<Skill>> listarTodas() {
        return ApiResponse.success("Skills listadas com sucesso.", skillDAO.findAll());
    }

    public ApiResponse<Skill> buscarPorId(int idSkill) {
        Skill skill = skillDAO.findById(idSkill);

        if (skill == null)
            return ApiResponse.error("Skill não encontrada.");

        return ApiResponse.success("Skill encontrada.", skill);
    }

    public ApiResponse<Void> atualizar(int idSkill, SkillDTO dto) {
        if (dto == null || isBlank(dto.getName()))
            return ApiResponse.error("Nome da skill obrigatório.");

        Skill atual = skillDAO.findById(idSkill);

        if (atual == null)
            return ApiResponse.error("Skill não encontrada.");

        Skill existente = skillDAO.findByName(dto.getName());

        if (existente != null && !existente.getIdSkill().equals(idSkill))
            return ApiResponse.error("Já existe outra skill com esse nome.");

        atual.setName(dto.getName());

        if (!skillDAO.update(atual))
            return ApiResponse.error("Erro ao atualizar skill.");

        return ApiResponse.success("Skill atualizada com sucesso.");
    }

    public ApiResponse<Void> deletar(int idSkill) {
        if (skillDAO.findById(idSkill) == null)
            return ApiResponse.error("Skill não encontrada.");

        if (!skillDAO.delete(idSkill))
            return ApiResponse.error("Erro ao deletar skill.");

        return ApiResponse.success("Skill deletada com sucesso.");
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }
}