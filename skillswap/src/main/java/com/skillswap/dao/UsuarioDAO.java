package com.skillswap.dao;

import com.skillswap.database.Database;
import com.skillswap.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean save(Usuario usuario) {
        String sql = """
            INSERT INTO usuario
            (email, senha, bio, nome, nota, num_avaliacoes)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, senhaHash);
            stmt.setString(3, usuario.getBio());
            stmt.setString(4, usuario.getNome());

            if (usuario.getNota() != null) stmt.setBigDecimal(5, usuario.getNota());
            else stmt.setNull(5, Types.NUMERIC);

            stmt.setInt(6, usuario.getNumAvaliacoes() != null ? usuario.getNumAvaliacoes() : 0);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState()))
                System.err.println("E-mail já cadastrado.");
            else {
                System.err.println("Erro ao salvar usuário: " + e.getMessage());
                e.printStackTrace();
            }

            return false;
        }
    }

    public Usuario findByEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Usuario findById(int idUsuario) {
        try (Connection conn = Database.getConnection()) {
            return findById(conn, idUsuario);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Usuario findById(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUsuario(rs);
            }
        }

        return null;
    }

    public List<Usuario> findAll() {
        String sql = "SELECT * FROM usuario ORDER BY id_usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next())
                usuarios.add(mapResultSetToUsuario(rs));

        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    public boolean update(Usuario usuario) {
        String sql = """
            UPDATE usuario
            SET email = ?, bio = ?, nome = ?
            WHERE id_usuario = ?
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getBio());
            stmt.setString(3, usuario.getNome());
            stmt.setInt(4, usuario.getIdUsuario());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int idUsuario, String novaSenhaPlana) {
        String sql = "UPDATE usuario SET senha = ? WHERE id_usuario = ?";
        String senhaHash = BCrypt.hashpw(novaSenhaPlana, BCrypt.gensalt());

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, senhaHash);
            stmt.setInt(2, idUsuario);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar senha: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNota(int idUsuario, float novaMedia, int novoNumAvaliacoes) {
        try (Connection conn = Database.getConnection()) {
            return updateNota(conn, idUsuario, novaMedia, novoNumAvaliacoes);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar nota: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNota(Connection conn, int idUsuario, float novaMedia, int novoNumAvaliacoes)
            throws SQLException {

        String sql = """
            UPDATE usuario
            SET nota = ?, num_avaliacoes = ?
            WHERE id_usuario = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, novaMedia);
            stmt.setInt(2, novoNumAvaliacoes);
            stmt.setInt(3, idUsuario);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkPassword(Usuario usuario, String senhaPlana) {
        if (usuario == null || usuario.getSenha() == null || senhaPlana == null)
            return false;

        return BCrypt.checkpw(senhaPlana, usuario.getSenha());
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setBio(rs.getString("bio"));
        usuario.setNome(rs.getString("nome"));
        usuario.setNota(rs.getBigDecimal("nota"));
        usuario.setNumAvaliacoes(rs.getInt("num_avaliacoes"));

        return usuario;
    }
}