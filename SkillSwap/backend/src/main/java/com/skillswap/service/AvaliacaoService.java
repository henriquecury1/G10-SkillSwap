package com.skillswap.service;

import com.skillswap.dao.AvaliacaoDAO;
import com.skillswap.dao.UsuarioDAO;
import com.skillswap.database.Database;
import com.skillswap.model.Avaliacao;
import com.skillswap.model.Usuario;
import com.skillswap.response.ApiResponse;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AvaliacaoService {

    private final AvaliacaoDAO avaliacaoDAO;
    private final UsuarioDAO usuarioDAO;

    public AvaliacaoService() {
        this.avaliacaoDAO = new AvaliacaoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public ApiResponse<Void> avaliarUsuario(int avaliador, int avaliado, Float nota) {
        if (nota == null)
            return ApiResponse.error("Nota obrigatória.");

        if (avaliador == avaliado)
            return ApiResponse.error("Não é possível avaliar a si mesmo.");

        if (!notaValida(nota))
            return ApiResponse.error("Nota deve estar entre 0 e 5.");

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            Usuario usuarioAvaliador = usuarioDAO.findById(conn, avaliador);
            Usuario usuarioAvaliado = usuarioDAO.findById(conn, avaliado);

            if (usuarioAvaliador == null || usuarioAvaliado == null) {
                conn.rollback();
                return ApiResponse.error("Usuário não encontrado.");
            }

            Avaliacao existente = avaliacaoDAO.findByAvaliadorAndAvaliado(conn, avaliador, avaliado);

            if (existente != null) {
                ApiResponse<Void> response = editarAvaliacaoExistente(conn, existente, nota);

                if (response.isSuccess()) conn.commit();
                else conn.rollback();

                return response;
            }

            Avaliacao nova = new Avaliacao(
                    UUID.randomUUID().toString(),
                    avaliado,
                    avaliador,
                    nota,
                    LocalDateTime.now()
            );

            if (!avaliacaoDAO.save(conn, nova)) {
                conn.rollback();
                return ApiResponse.error("Erro ao salvar avaliação.");
            }

            if (!adicionarAvaliacaoNaMedia(conn, usuarioAvaliado, nota)) {
                conn.rollback();
                return ApiResponse.error("Erro ao atualizar média do usuário.");
            }

            conn.commit();
            return ApiResponse.success("Avaliação salva com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao avaliar usuário: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("Erro interno ao avaliar usuário.");
        }
    }

    public ApiResponse<Void> editarAvaliacao(int avaliador, int avaliado, Float novaNota) {
        if (novaNota == null)
            return ApiResponse.error("Nota obrigatória.");

        if (!notaValida(novaNota))
            return ApiResponse.error("Nota deve estar entre 0 e 5.");

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            Avaliacao avaliacao = avaliacaoDAO.findByAvaliadorAndAvaliado(conn, avaliador, avaliado);

            if (avaliacao == null) {
                conn.rollback();
                return ApiResponse.error("Avaliação não encontrada.");
            }

            ApiResponse<Void> response = editarAvaliacaoExistente(conn, avaliacao, novaNota);

            if (response.isSuccess()) conn.commit();
            else conn.rollback();

            return response;

        } catch (Exception e) {
            System.err.println("Erro ao editar avaliação: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("Erro interno ao editar avaliação.");
        }
    }

    public ApiResponse<List<Avaliacao>> listarAvaliacoesRecebidas(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        return ApiResponse.success(
                "Avaliações listadas com sucesso.",
                avaliacaoDAO.findByAvaliado(idUsuario)
        );
    }

    private ApiResponse<Void> editarAvaliacaoExistente(Connection conn, Avaliacao avaliacao, float novaNota)
            throws Exception {

        float notaAntiga = avaliacao.getNota();

        avaliacao.setNota(novaNota);
        avaliacao.setDataHora(LocalDateTime.now());

        Usuario avaliado = usuarioDAO.findById(conn, avaliacao.getAvaliado());

        if (avaliado == null)
            return ApiResponse.error("Usuário avaliado não encontrado.");

        if (!avaliacaoDAO.update(conn, avaliacao))
            return ApiResponse.error("Erro ao atualizar avaliação.");

        if (!editarAvaliacaoNaMedia(conn, avaliado, notaAntiga, novaNota))
            return ApiResponse.error("Erro ao recalcular média.");

        return ApiResponse.success("Avaliação atualizada com sucesso.");
    }

    private boolean adicionarAvaliacaoNaMedia(Connection conn, Usuario usuario, float novaNota)
            throws Exception {

        int numAvaliacoes = usuario.getNumAvaliacoes() != null ? usuario.getNumAvaliacoes() : 0;
        BigDecimal notaAtualBD = usuario.getNota();

        float mediaAtual = notaAtualBD != null ? notaAtualBD.floatValue() : 0;
        int novoNumAvaliacoes = numAvaliacoes + 1;
        float novaMedia = ((mediaAtual * numAvaliacoes) + novaNota) / novoNumAvaliacoes;

        return usuarioDAO.updateNota(conn, usuario.getIdUsuario(), novaMedia, novoNumAvaliacoes);
    }

    private boolean editarAvaliacaoNaMedia(Connection conn, Usuario usuario, float notaAntiga, float notaNova)
            throws Exception {

        if (usuario.getNumAvaliacoes() == null || usuario.getNumAvaliacoes() <= 0)
            return false;

        int numAvaliacoes = usuario.getNumAvaliacoes();
        float mediaAtual = usuario.getNota() != null ? usuario.getNota().floatValue() : 0;
        float novaMedia = ((mediaAtual * numAvaliacoes) - notaAntiga + notaNova) / numAvaliacoes;

        return usuarioDAO.updateNota(conn, usuario.getIdUsuario(), novaMedia, numAvaliacoes);
    }

    private boolean notaValida(float nota) {
        return nota >= 0 && nota <= 5;
    }
}