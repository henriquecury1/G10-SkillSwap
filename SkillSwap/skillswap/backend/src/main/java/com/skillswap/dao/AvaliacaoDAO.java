package com.skillswap.dao;

import com.skillswap.database.Database;
import com.skillswap.model.Avaliacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO {

    public boolean save(Avaliacao avaliacao) {
        try (Connection conn = Database.getConnection()) {
            return save(conn, avaliacao);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar avaliação: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(Connection conn, Avaliacao avaliacao) throws SQLException {
        String sql = """
            INSERT INTO avaliacao
            (id_avaliacao, avaliado, avaliador, nota, data_hora)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avaliacao.getIdAvaliacao());
            stmt.setInt(2, avaliacao.getAvaliado());
            stmt.setInt(3, avaliacao.getAvaliador());
            stmt.setFloat(4, avaliacao.getNota());
            stmt.setTimestamp(5, avaliacao.getDataHora() != null
                    ? Timestamp.valueOf(avaliacao.getDataHora())
                    : new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
            return true;
        }
    }

    public Avaliacao findById(String idAvaliacao) {
        String sql = "SELECT * FROM avaliacao WHERE id_avaliacao = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idAvaliacao);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToAvaliacao(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliação: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Avaliacao findByAvaliadorAndAvaliado(int avaliador, int avaliado) {
        try (Connection conn = Database.getConnection()) {
            return findByAvaliadorAndAvaliado(conn, avaliador, avaliado);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliação: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Avaliacao findByAvaliadorAndAvaliado(Connection conn, int avaliador, int avaliado)
            throws SQLException {

        String sql = "SELECT * FROM avaliacao WHERE avaliador = ? AND avaliado = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, avaliador);
            stmt.setInt(2, avaliado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToAvaliacao(rs);
            }
        }

        return null;
    }

    public List<Avaliacao> findByAvaliado(int idUsuario) {
        String sql = """
            SELECT *
            FROM avaliacao
            WHERE avaliado = ?
            ORDER BY data_hora DESC
            """;

        List<Avaliacao> avaliacoes = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    avaliacoes.add(mapResultSetToAvaliacao(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar avaliações: " + e.getMessage());
            e.printStackTrace();
        }

        return avaliacoes;
    }

    public boolean update(Avaliacao avaliacao) {
        try (Connection conn = Database.getConnection()) {
            return update(conn, avaliacao);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar avaliação: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Connection conn, Avaliacao avaliacao) throws SQLException {
        String sql = """
            UPDATE avaliacao
            SET nota = ?, data_hora = ?
            WHERE id_avaliacao = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, avaliacao.getNota());
            stmt.setTimestamp(2, Timestamp.valueOf(avaliacao.getDataHora()));
            stmt.setString(3, avaliacao.getIdAvaliacao());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String idAvaliacao) {
        String sql = "DELETE FROM avaliacao WHERE id_avaliacao = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idAvaliacao);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar avaliação: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Avaliacao mapResultSetToAvaliacao(ResultSet rs) throws SQLException {
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setIdAvaliacao(rs.getString("id_avaliacao"));
        avaliacao.setAvaliado(rs.getInt("avaliado"));
        avaliacao.setAvaliador(rs.getInt("avaliador"));
        avaliacao.setNota(rs.getFloat("nota"));

        Timestamp timestamp = rs.getTimestamp("data_hora");
        if (timestamp != null)
            avaliacao.setDataHora(timestamp.toLocalDateTime());

        return avaliacao;
    }
}