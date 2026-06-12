package com.skillswap.dao;

import com.skillswap.database.Database;
import com.skillswap.model.Qualificacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QualificacaoDAO {

    public boolean save(Qualificacao qualificacao) {
        String sql = """
                INSERT INTO qualificacao
                (qualificado, skill)
                VALUES (?, ?)
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, qualificacao.getQualificado());
            stmt.setInt(2, qualificacao.getSkill());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                System.err.println("Usuário já possui essa skill.");
            } else {
                System.err.println("Erro ao salvar qualificação: " + e.getMessage());
            }
            return false;
        }
    }

    public List<Qualificacao> findByUsuario(int idUsuario) {
        String sql = """
                SELECT qualificado, skill
                FROM qualificacao
                WHERE qualificado = ?
                """;

        List<Qualificacao> qualificacoes = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    qualificacoes.add(mapResultSetToQualificacao(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar qualificações do usuário: " + e.getMessage());
        }

        return qualificacoes;
    }

    public List<Qualificacao> findBySkill(int idSkill) {
        String sql = """
                SELECT qualificado, skill
                FROM qualificacao
                WHERE skill = ?
                """;

        List<Qualificacao> qualificacoes = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSkill);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    qualificacoes.add(mapResultSetToQualificacao(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários por skill: " + e.getMessage());
        }

        return qualificacoes;
    }

    public boolean delete(int idUsuario, int idSkill) {
        String sql = """
                DELETE FROM qualificacao
                WHERE qualificado = ? AND skill = ?
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idSkill);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar qualificação: " + e.getMessage());
            return false;
        }
    }

    private Qualificacao mapResultSetToQualificacao(ResultSet rs) throws SQLException {
        Qualificacao qualificacao = new Qualificacao();

        qualificacao.setQualificado(rs.getInt("qualificado"));
        qualificacao.setSkill(rs.getInt("skill"));

        return qualificacao;
    }
}