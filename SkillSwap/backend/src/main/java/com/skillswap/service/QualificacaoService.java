package com.skillswap.service;

import com.skillswap.dao.QualificacaoDAO;
import com.skillswap.dao.SkillDAO;
import com.skillswap.dao.UsuarioDAO;
import com.skillswap.model.Qualificacao;
import com.skillswap.model.Skill;
import com.skillswap.model.Usuario;
import com.skillswap.response.ApiResponse;

import java.util.ArrayList;
import java.util.List;

public class QualificacaoService {

    private final QualificacaoDAO qualificacaoDAO;
    private final UsuarioDAO usuarioDAO;
    private final SkillDAO skillDAO;

    public QualificacaoService() {
        this.qualificacaoDAO = new QualificacaoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.skillDAO = new SkillDAO();
    }

    public ApiResponse<Void> adicionarSkillAoUsuario(int idUsuario, int idSkill) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        if (skillDAO.findById(idSkill) == null)
            return ApiResponse.error("Skill não encontrada.");

        if (usuarioPossuiSkill(idUsuario, idSkill))
            return ApiResponse.error("Usuário já possui essa skill.");

        Qualificacao qualificacao = new Qualificacao(idUsuario, idSkill);

        if (!qualificacaoDAO.save(qualificacao))
            return ApiResponse.error("Erro ao adicionar skill ao usuário.");

        return ApiResponse.success("Skill adicionada ao usuário com sucesso.");
    }

    public ApiResponse<List<Qualificacao>> listarSkillsDoUsuario(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        return ApiResponse.success(
                "Skills do usuário listadas com sucesso.",
                qualificacaoDAO.findByUsuario(idUsuario)
        );
    }

    public ApiResponse<List<Skill>> listarSkillsDetalhadasDoUsuario(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        List<Qualificacao> qualificacoes = qualificacaoDAO.findByUsuario(idUsuario);
        List<Skill> skills = new ArrayList<>();

        for (Qualificacao qualificacao : qualificacoes) {
            Skill skill = skillDAO.findById(qualificacao.getSkill());

            if (skill != null)
                skills.add(skill);
        }

        return ApiResponse.success("Skills detalhadas do usuário listadas com sucesso.", skills);
    }

    public ApiResponse<List<Qualificacao>> listarUsuariosPorSkill(int idSkill) {
        if (skillDAO.findById(idSkill) == null)
            return ApiResponse.error("Skill não encontrada.");

        return ApiResponse.success(
                "Usuários por skill listados com sucesso.",
                qualificacaoDAO.findBySkill(idSkill)
        );
    }

    public ApiResponse<List<Usuario>> listarUsuariosDetalhadosPorSkill(int idSkill) {
        if (skillDAO.findById(idSkill) == null)
            return ApiResponse.error("Skill não encontrada.");

        List<Qualificacao> qualificacoes = qualificacaoDAO.findBySkill(idSkill);
        List<Usuario> usuarios = new ArrayList<>();

        for (Qualificacao qualificacao : qualificacoes) {
            Usuario usuario = usuarioDAO.findById(qualificacao.getQualificado());

            if (usuario != null) {
                usuario.setSenha(null);
                usuarios.add(usuario);
            }
        }

        return ApiResponse.success("Usuários detalhados por skill listados com sucesso.", usuarios);
    }

    public ApiResponse<Void> removerSkillDoUsuario(int idUsuario, int idSkill) {
        if (!usuarioPossuiSkill(idUsuario, idSkill))
            return ApiResponse.error("Usuário não possui essa skill.");

        if (!qualificacaoDAO.delete(idUsuario, idSkill))
            return ApiResponse.error("Erro ao remover skill do usuário.");

        return ApiResponse.success("Skill removida do usuário com sucesso.");
    }

    private boolean usuarioPossuiSkill(int idUsuario, int idSkill) {
        return qualificacaoDAO.findByUsuario(idUsuario)
                .stream()
                .anyMatch(q -> q.getSkill().equals(idSkill));
    }
}
