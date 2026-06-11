package com.skillswap.service;

import com.skillswap.dao.AmizadeDAO;
import com.skillswap.dao.UsuarioDAO;
import com.skillswap.dto.AmizadeDetalhadaDTO;
import com.skillswap.model.Amizade;
import com.skillswap.model.Usuario;
import com.skillswap.response.ApiResponse;

import java.util.ArrayList;
import java.util.List;

public class AmizadeService {

    public static final int PENDENTE = 0;
    public static final int ACEITA = 1;
    public static final int RECUSADA = 2;
    public static final int BLOQUEADO = 3;

    private final AmizadeDAO amizadeDAO;
    private final UsuarioDAO usuarioDAO;

    public AmizadeService() {
        this.amizadeDAO = new AmizadeDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public ApiResponse<Void> enviarSolicitacao(int solicitante, int destinatario) {
        if (solicitante == destinatario)
            return ApiResponse.error("Não é possível enviar solicitação para si mesmo.");

        if (!usuariosExistem(solicitante, destinatario))
            return ApiResponse.error("Usuário não encontrado.");

        Amizade existente = amizadeDAO.findByUsuarios(solicitante, destinatario);

        if (existente != null) {
            if (existente.getStatus().equals(BLOQUEADO))
                return ApiResponse.error("Interação bloqueada entre usuários.");

            if (existente.getStatus().equals(ACEITA))
                return ApiResponse.error("Usuários já são amigos.");

            if (existente.getStatus().equals(PENDENTE))
                return ApiResponse.error("Solicitação já está pendente.");

            existente.setStatus(PENDENTE);
            existente.setUsuario1(solicitante);
            existente.setUsuario2(destinatario);

            if (!amizadeDAO.update(existente))
                return ApiResponse.error("Erro ao reenviar solicitação.");

            return ApiResponse.success("Solicitação enviada novamente.");
        }

        Amizade amizade = new Amizade(null, PENDENTE, solicitante, destinatario);

        if (!amizadeDAO.save(amizade))
            return ApiResponse.error("Erro ao enviar solicitação.");

        return ApiResponse.success("Solicitação enviada com sucesso.");
    }

    public ApiResponse<Void> aceitarSolicitacao(int idAmizade, int usuarioLogado) {
        Amizade amizade = amizadeDAO.findById(idAmizade);

        if (amizade == null)
            return ApiResponse.error("Solicitação não encontrada.");

        if (!amizade.getStatus().equals(PENDENTE))
            return ApiResponse.error("Solicitação não está pendente.");

        if (!amizade.getUsuario2().equals(usuarioLogado))
            return ApiResponse.error("Apenas o destinatário pode aceitar a solicitação.");

        amizade.setStatus(ACEITA);

        if (!amizadeDAO.update(amizade))
            return ApiResponse.error("Erro ao aceitar solicitação.");

        return ApiResponse.success("Solicitação aceita.");
    }

    public ApiResponse<Void> recusarSolicitacao(int idAmizade, int usuarioLogado) {
        Amizade amizade = amizadeDAO.findById(idAmizade);

        if (amizade == null)
            return ApiResponse.error("Solicitação não encontrada.");

        if (!amizade.getStatus().equals(PENDENTE))
            return ApiResponse.error("Solicitação não está pendente.");

        if (!amizade.getUsuario2().equals(usuarioLogado))
            return ApiResponse.error("Apenas o destinatário pode recusar a solicitação.");

        amizade.setStatus(RECUSADA);

        if (!amizadeDAO.update(amizade))
            return ApiResponse.error("Erro ao recusar solicitação.");

        return ApiResponse.success("Solicitação recusada.");
    }

    public ApiResponse<Void> bloquearUsuario(int bloqueador, int bloqueado) {
        if (bloqueador == bloqueado)
            return ApiResponse.error("Não é possível bloquear a si mesmo.");

        if (!usuariosExistem(bloqueador, bloqueado))
            return ApiResponse.error("Usuário não encontrado.");

        Amizade amizade = amizadeDAO.findByUsuarios(bloqueador, bloqueado);

        if (amizade != null && amizade.getStatus().equals(BLOQUEADO)) {
            if (amizade.getUsuario1().equals(bloqueador))
                return ApiResponse.success("Usuário já está bloqueado.");

            return ApiResponse.error("Interação bloqueada entre usuários.");
        }

        if (amizade == null) {
            amizade = new Amizade(null, BLOQUEADO, bloqueador, bloqueado);

            if (!amizadeDAO.save(amizade))
                return ApiResponse.error("Erro ao bloquear usuário.");

            return ApiResponse.success("Usuário bloqueado.");
        }

        amizade.setStatus(BLOQUEADO);
        amizade.setUsuario1(bloqueador);
        amizade.setUsuario2(bloqueado);

        if (!amizadeDAO.update(amizade))
            return ApiResponse.error("Erro ao bloquear usuário.");

        return ApiResponse.success("Usuário bloqueado.");
    }

    public ApiResponse<Void> desbloquearUsuario(int bloqueador, int bloqueado) {
        Amizade amizade = amizadeDAO.findByUsuarios(bloqueador, bloqueado);

        if (amizade == null)
            return ApiResponse.error("Bloqueio não encontrado.");

        if (!amizade.getStatus().equals(BLOQUEADO))
            return ApiResponse.error("Essa relação não está bloqueada.");

        if (!amizade.getUsuario1().equals(bloqueador))
            return ApiResponse.error("Apenas quem bloqueou pode desbloquear.");

        amizade.setStatus(RECUSADA);

        if (!amizadeDAO.update(amizade))
            return ApiResponse.error("Erro ao desbloquear usuário.");

        return ApiResponse.success("Usuário desbloqueado.");
    }

    public ApiResponse<Void> removerAmizade(int idAmizade, int usuarioLogado) {
        Amizade amizade = amizadeDAO.findById(idAmizade);

        if (amizade == null)
            return ApiResponse.error("Amizade não encontrada.");

        if (!amizade.getStatus().equals(ACEITA))
            return ApiResponse.error("Essa relação não é uma amizade aceita.");

        if (!usuarioPertenceAmizade(amizade, usuarioLogado))
            return ApiResponse.error("Usuário não pertence a essa amizade.");

        amizade.setStatus(RECUSADA);

        if (!amizadeDAO.update(amizade))
            return ApiResponse.error("Erro ao remover amizade.");

        return ApiResponse.success("Amizade removida.");
    }

    public ApiResponse<List<Amizade>> listarAmizadesDoUsuario(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        return ApiResponse.success(
                "Amizades listadas com sucesso.",
                amizadeDAO.findByUsuario(idUsuario)
        );
    }

    public ApiResponse<List<AmizadeDetalhadaDTO>> listarAmizadesDetalhadasDoUsuario(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        List<Amizade> amizades = amizadeDAO.findByUsuario(idUsuario);
        List<AmizadeDetalhadaDTO> detalhes = new ArrayList<>();

        for (Amizade amizade : amizades) {
            Integer idOutroUsuario = amizade.getUsuario1().equals(idUsuario)
                    ? amizade.getUsuario2()
                    : amizade.getUsuario1();

            Usuario outroUsuario = usuarioDAO.findById(idOutroUsuario);

            if (outroUsuario != null) {
                outroUsuario.setSenha(null);

                detalhes.add(new AmizadeDetalhadaDTO(
                        amizade.getIdAmizade(),
                        amizade.getStatus(),
                        amizade.getUsuario1(),
                        amizade.getUsuario2(),
                        outroUsuario
                ));
            }
        }

        return ApiResponse.success("Amizades detalhadas listadas com sucesso.", detalhes);
    }

    public ApiResponse<List<Amizade>> listarSolicitacoesRecebidas(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        return ApiResponse.success(
                "Solicitações recebidas listadas com sucesso.",
                amizadeDAO.findSolicitacoesRecebidas(idUsuario)
        );
    }

    public ApiResponse<List<AmizadeDetalhadaDTO>> listarSolicitacoesRecebidasDetalhadas(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        List<Amizade> solicitacoes = amizadeDAO.findSolicitacoesRecebidas(idUsuario);
        List<AmizadeDetalhadaDTO> detalhes = new ArrayList<>();

        for (Amizade amizade : solicitacoes) {
            Usuario solicitante = usuarioDAO.findById(amizade.getUsuario1());

            if (solicitante != null) {
                solicitante.setSenha(null);

                detalhes.add(new AmizadeDetalhadaDTO(
                        amizade.getIdAmizade(),
                        amizade.getStatus(),
                        amizade.getUsuario1(),
                        amizade.getUsuario2(),
                        solicitante
                ));
            }
        }

        return ApiResponse.success("Solicitações recebidas detalhadas listadas com sucesso.", detalhes);
    }

    public ApiResponse<List<Amizade>> listarSolicitacoesEnviadas(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        return ApiResponse.success(
                "Solicitações enviadas listadas com sucesso.",
                amizadeDAO.findSolicitacoesEnviadas(idUsuario)
        );
    }

    public ApiResponse<List<AmizadeDetalhadaDTO>> listarSolicitacoesEnviadasDetalhadas(int idUsuario) {
        if (usuarioDAO.findById(idUsuario) == null)
            return ApiResponse.error("Usuário não encontrado.");

        List<Amizade> solicitacoes = amizadeDAO.findSolicitacoesEnviadas(idUsuario);
        List<AmizadeDetalhadaDTO> detalhes = new ArrayList<>();

        for (Amizade amizade : solicitacoes) {
            Usuario destinatario = usuarioDAO.findById(amizade.getUsuario2());

            if (destinatario != null) {
                destinatario.setSenha(null);

                detalhes.add(new AmizadeDetalhadaDTO(
                        amizade.getIdAmizade(),
                        amizade.getStatus(),
                        amizade.getUsuario1(),
                        amizade.getUsuario2(),
                        destinatario
                ));
            }
        }

        return ApiResponse.success("Solicitações enviadas detalhadas listadas com sucesso.", detalhes);
    }

    public boolean amizadeAceitaEntre(int usuario1, int usuario2) {
        Amizade amizade = amizadeDAO.findByUsuarios(usuario1, usuario2);
        return amizade != null && amizade.getStatus().equals(ACEITA);
    }

    public boolean interacaoBloqueadaEntre(int usuario1, int usuario2) {
        Amizade amizade = amizadeDAO.findByUsuarios(usuario1, usuario2);
        return amizade != null && amizade.getStatus().equals(BLOQUEADO);
    }

    private boolean usuariosExistem(int usuario1, int usuario2) {
        return usuarioDAO.findById(usuario1) != null
                && usuarioDAO.findById(usuario2) != null;
    }

    private boolean usuarioPertenceAmizade(Amizade amizade, int usuario) {
        return amizade.getUsuario1().equals(usuario)
                || amizade.getUsuario2().equals(usuario);
    }
}
