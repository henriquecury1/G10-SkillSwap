package com.skillswap.dao;

import com.skillswap.database.Database;
import com.skillswap.model.Amizade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmizadeDAO {

    public boolean save(Amizade amizade) {
        String sql = "INSERT INTO amizade (status, usuario1, usuario2) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, amizade.getStatus());
            stmt.setInt(2, amizade.getUsuario1());
            stmt.setInt(3, amizade.getUsuario2());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState()))
                System.err.println("Relação entre usuários já existe.");
            else {
                System.err.println("Erro ao salvar amizade: " + e.getMessage());
                e.printStackTrace();
            }

            return false;
        }
    }

    public Amizade findById(int idAmizade) {
        String sql = "SELECT * FROM amizade WHERE id_amizade = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAmizade);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToAmizade(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar amizade: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Amizade findByUsuarios(int usuario1, int usuario2) {
        String sql = """
            SELECT *
            FROM amizade
            WHERE (usuario1 = ? AND usuario2 = ?)
               OR (usuario1 = ? AND usuario2 = ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario1);
            stmt.setInt(2, usuario2);
            stmt.setInt(3, usuario2);
            stmt.setInt(4, usuario1);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToAmizade(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar amizade entre usuários: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Amizade> findAll() {
        String sql = "SELECT * FROM amizade ORDER BY id_amizade";
        List<Amizade> amizades = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next())
                amizades.add(mapResultSetToAmizade(rs));

        } catch (SQLException e) {
            System.err.println("Erro ao listar amizades: " + e.getMessage());
            e.printStackTrace();
        }

        return amizades;
    }

    public List<Amizade> findByUsuario(int idUsuario) {
        String sql = """
            SELECT *
            FROM amizade
            WHERE usuario1 = ? OR usuario2 = ?
            ORDER BY id_amizade
            """;

        List<Amizade> amizades = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    amizades.add(mapResultSetToAmizade(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar amizades do usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return amizades;
    }

    public List<Amizade> findSolicitacoesRecebidas(int idUsuario) {
        String sql = """
            SELECT *
            FROM amizade
            WHERE usuario2 = ? AND status = 0
            ORDER BY id_amizade
            """;

        List<Amizade> solicitacoes = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    solicitacoes.add(mapResultSetToAmizade(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar solicitações recebidas: " + e.getMessage());
            e.printStackTrace();
        }

        return solicitacoes;
    }

    public List<Amizade> findSolicitacoesEnviadas(int idUsuario) {
        String sql = """
            SELECT *
            FROM amizade
            WHERE usuario1 = ? AND status = 0
            ORDER BY id_amizade
            """;

        List<Amizade> solicitacoes = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    solicitacoes.add(mapResultSetToAmizade(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar solicitações enviadas: " + e.getMessage());
            e.printStackTrace();
        }

        return solicitacoes;
    }

    public boolean update(Amizade amizade) {
        String sql = """
            UPDATE amizade
            SET status = ?, usuario1 = ?, usuario2 = ?
            WHERE id_amizade = ?
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, amizade.getStatus());
            stmt.setInt(2, amizade.getUsuario1());
            stmt.setInt(3, amizade.getUsuario2());
            stmt.setInt(4, amizade.getIdAmizade());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar amizade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int idAmizade, int status) {
        String sql = "UPDATE amizade SET status = ? WHERE id_amizade = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, status);
            stmt.setInt(2, idAmizade);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status da amizade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int idAmizade) {
        String sql = "DELETE FROM amizade WHERE id_amizade = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAmizade);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar amizade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Amizade mapResultSetToAmizade(ResultSet rs) throws SQLException {
        Amizade amizade = new Amizade();

        amizade.setIdAmizade(rs.getInt("id_amizade"));
        amizade.setStatus(rs.getInt("status"));
        amizade.setUsuario1(rs.getInt("usuario1"));
        amizade.setUsuario2(rs.getInt("usuario2"));

        return amizade;
    }
}