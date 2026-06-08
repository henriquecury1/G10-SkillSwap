package com.skillswap.dao;

import com.skillswap.database.Database;
import com.skillswap.model.Mensagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensagemDAO {

    public boolean save(Mensagem mensagem) {
        String sql = """
            INSERT INTO mensagem
            (tipo, conteudo, data_hora, remetente, amizade)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mensagem.getTipo());
            stmt.setString(2, mensagem.getConteudo());
            stmt.setTimestamp(3, mensagem.getDataHora() != null
                    ? Timestamp.valueOf(mensagem.getDataHora())
                    : new Timestamp(System.currentTimeMillis()));
            stmt.setInt(4, mensagem.getRemetente());
            stmt.setInt(5, mensagem.getAmizade());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar mensagem: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Mensagem findById(int idMensagem) {
        String sql = "SELECT * FROM mensagem WHERE id_mensagem = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMensagem);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToMensagem(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar mensagem: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Mensagem> findByAmizade(int idAmizade) {
        String sql = """
            SELECT *
            FROM mensagem
            WHERE amizade = ?
            ORDER BY data_hora
            """;

        List<Mensagem> mensagens = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAmizade);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    mensagens.add(mapResultSetToMensagem(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar mensagens da amizade: " + e.getMessage());
            e.printStackTrace();
        }

        return mensagens;
    }

    public boolean delete(int idMensagem) {
        String sql = "DELETE FROM mensagem WHERE id_mensagem = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMensagem);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar mensagem: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Mensagem mapResultSetToMensagem(ResultSet rs) throws SQLException {
        Mensagem mensagem = new Mensagem();

        mensagem.setIdMensagem(rs.getInt("id_mensagem"));
        mensagem.setTipo(rs.getInt("tipo"));
        mensagem.setConteudo(rs.getString("conteudo"));

        Timestamp timestamp = rs.getTimestamp("data_hora");
        if (timestamp != null)
            mensagem.setDataHora(timestamp.toLocalDateTime());

        mensagem.setRemetente(rs.getInt("remetente"));
        mensagem.setAmizade(rs.getInt("amizade"));

        return mensagem;
    }
}