package com.skillswap.service;

import com.skillswap.dao.QualificacaoDAO;
import com.skillswap.dao.SkillDAO;
import com.skillswap.dao.UsuarioDAO;
import com.skillswap.dto.MatchResultDTO;
import com.skillswap.dto.MatchingResponseDTO;
import com.skillswap.model.Qualificacao;
import com.skillswap.model.Skill;
import com.skillswap.model.Usuario;

import java.util.*;
import java.util.stream.Collectors;

public class MatchingService {

    private final UsuarioDAO usuarioDAO;
    private final QualificacaoDAO qualificacaoDAO;
    private final SkillDAO skillDAO;

    public MatchingService() {
        this.usuarioDAO = new UsuarioDAO();
        this.qualificacaoDAO = new QualificacaoDAO();
        this.skillDAO = new SkillDAO();
    }

    public MatchingResponseDTO gerarMatches(int idUsuarioLogado, List<String> horariosUsuario) {

        // Skills do usuário logado
        List<Skill> mySkills = resolveSkills(idUsuarioLogado);

        // Todos os outros usuários
        List<Usuario> outros = usuarioDAO.findAll()
                .stream()
                .filter(u -> u.getIdUsuario() != idUsuarioLogado)
                .collect(Collectors.toList());

        // Calcular score para cada usuário
        List<MatchResultDTO> results = outros.stream().map(u -> {
            List<Skill> theirSkills = resolveSkills(u.getIdUsuario());

            // Skills em comum (match por nome, case-insensitive, parcial)
            List<String> compatible = mySkills.stream()
                    .filter(ms -> theirSkills.stream().anyMatch(ts ->
                            ts.getName().toLowerCase().contains(ms.getName().toLowerCase()) ||
                            ms.getName().toLowerCase().contains(ts.getName().toLowerCase())))
                    .map(Skill::getName)
                    .collect(Collectors.toList());

            // Score: 25 pontos por skill compatível, bônus por ter mais skills
            int scoreSkills = Math.min(75, compatible.size() * 25);
            int scoreBonus  = Math.min(15, theirSkills.size() * 3);
            int score       = Math.max(20, Math.min(98, scoreSkills + scoreBonus));

            // Horários em comum (os informados pelo usuário são usados como referência)
            // Como não há tabela de horários no banco, exibimos os horários que o usuário selecionou
            List<String> overlap = horariosUsuario.subList(0, Math.min(2, horariosUsuario.size()));

            // Texto de razão
            String reason = buildReason(u.getNome(), mySkills, theirSkills, compatible);

            return new MatchResultDTO(u, theirSkills, score, reason, compatible, overlap);
        })
        .sorted((a, b) -> b.getScore() - a.getScore())
        .limit(8)
        .collect(Collectors.toList());

        String analysis = buildAnalysis(results);

        return new MatchingResponseDTO(analysis, results);
    }

    // ── Monta texto de razão para cada match ─────────────────────────────────

    private String buildReason(String nome, List<Skill> mySkills, List<Skill> theirSkills, List<String> compatible) {
        if (compatible.isEmpty()) {
            if (theirSkills.isEmpty()) {
                return nome + " ainda não cadastrou skills, mas pode ser um bom parceiro para trocar conhecimentos.";
            }
            return nome + " tem um perfil diferente do seu, o que pode gerar uma troca de conhecimentos complementares.";
        }

        if (compatible.size() == 1) {
            return nome + " e você têm a skill \"" + compatible.get(0) + "\" em comum — ótimo ponto de partida para uma troca.";
        }

        String skillsStr = compatible.subList(0, Math.min(3, compatible.size()))
                .stream().collect(Collectors.joining(", "));
        return nome + " compartilha " + compatible.size() + " skills com você: " + skillsStr + ". Alta sinergia de conhecimentos.";
    }

    // ── Monta texto de análise geral ─────────────────────────────────────────

    private String buildAnalysis(List<MatchResultDTO> results) {
        if (results.isEmpty()) {
            return "Nenhum outro usuário encontrado na plataforma ainda. Convide colegas para participar!";
        }

        long highMatches = results.stream().filter(m -> m.getScore() >= 80).count();
        long midMatches  = results.stream().filter(m -> m.getScore() >= 60 && m.getScore() < 80).count();

        if (highMatches > 0) {
            return String.format(
                "Foram encontrados %d parceiro%s com alta compatibilidade (80%%+) e %d com compatibilidade média. " +
                "Os melhores matches têm skills que se complementam diretamente com o seu perfil.",
                highMatches, highMatches > 1 ? "s" : "", midMatches);
        }

        return String.format(
            "Foram encontrados %d parceiro%s compatível%s com seu perfil. " +
            "Quanto mais skills você cadastrar, mais precisos serão os matches.",
            results.size(), results.size() > 1 ? "s" : "", results.size() > 1 ? "s" : "");
    }

    // ── Helper: resolve skills de um usuário ─────────────────────────────────

    private List<Skill> resolveSkills(int idUsuario) {
        return qualificacaoDAO.findByUsuario(idUsuario).stream()
                .map(q -> skillDAO.findById(q.getSkill()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
