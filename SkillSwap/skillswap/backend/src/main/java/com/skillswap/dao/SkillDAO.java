package com.skillswap.dao;

import com.skillswap.database.Database;
import com.skillswap.model.Skill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {

    public boolean save(Skill skill) {
        String sql = "INSERT INTO skill (name) VALUES (?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, skill.getName());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar skill: " + e.getMessage());
            return false;
        }
    }

    public Skill findById(int idSkill) {
        String sql = "SELECT id_skill, name FROM skill WHERE id_skill = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSkill);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSkill(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar skill: " + e.getMessage());
        }

        return null;
    }

    public List<Skill> findAll() {
        String sql = "SELECT id_skill, name FROM skill ORDER BY id_skill";
        List<Skill> skills = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                skills.add(mapResultSetToSkill(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar skills: " + e.getMessage());
        }

        return skills;
    }

    public boolean update(Skill skill) {
        String sql = "UPDATE skill SET name = ? WHERE id_skill = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, skill.getName());
            stmt.setInt(2, skill.getIdSkill());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar skill: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int idSkill) {
        String sql = "DELETE FROM skill WHERE id_skill = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSkill);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar skill: " + e.getMessage());
            return false;
        }
    }

    private Skill mapResultSetToSkill(ResultSet rs) throws SQLException {
        Skill skill = new Skill();

        skill.setIdSkill(rs.getInt("id_skill"));
        skill.setName(rs.getString("name"));

        return skill;
    }
    
    public Skill findByName(String name) {
        String sql = "SELECT * FROM skill WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToSkill(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar skill por nome: " + e.getMessage());
        }

        return null;
    }
}