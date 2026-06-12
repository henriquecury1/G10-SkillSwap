package com.skillswap.service;

import com.skillswap.dao.AmizadeDAO;
import com.skillswap.dao.MensagemDAO;
import com.skillswap.model.Amizade;
import com.skillswap.model.Mensagem;
import com.skillswap.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

public class MensagemService {

    private final MensagemDAO mensagemDAO;
    private final AmizadeDAO amizadeDAO;

    public MensagemService() {
        this.mensagemDAO = new MensagemDAO();
        this.amizadeDAO = new AmizadeDAO();
    }

    public ApiResponse<Void> enviarMensagemTexto(int idAmizade, int remetente, String conteudo) {
        if (conteudo == null || conteudo.isBlank())
            return ApiResponse.error("Mensagem não pode ser vazia.");

        Amizade amizade = amizadeDAO.findById(idAmizade);

        if (amizade == null)
            return ApiResponse.error("Amizade não encontrada.");

        if (!amizade.getStatus().equals(AmizadeService.ACEITA))
            return ApiResponse.error("Mensagens só podem ser enviadas entre amizades aceitas.");

        if (!remetentePertenceAmizade(remetente, amizade))
            return ApiResponse.error("Remetente não pertence a essa amizade.");

        Mensagem mensagem = new Mensagem(null, 1, conteudo, LocalDateTime.now(), remetente, idAmizade);

        if (!mensagemDAO.save(mensagem))
            return ApiResponse.error("Erro ao enviar mensagem.");

        return ApiResponse.success("Mensagem enviada com sucesso.");
    }

    public ApiResponse<List<Mensagem>> listarMensagensDaAmizade(int idAmizade, int usuarioLogado) {
        Amizade amizade = amizadeDAO.findById(idAmizade);

        if (amizade == null)
            return ApiResponse.error("Amizade não encontrada.");

        if (!remetentePertenceAmizade(usuarioLogado, amizade))
            return ApiResponse.error("Usuário não pertence a essa amizade.");

        if (!amizade.getStatus().equals(AmizadeService.ACEITA))
            return ApiResponse.error("Mensagens só podem ser listadas em amizades aceitas.");

        return ApiResponse.success(
                "Mensagens listadas com sucesso.",
                mensagemDAO.findByAmizade(idAmizade)
        );
    }

    public ApiResponse<Void> deletarMensagem(int idMensagem, int usuarioLogado) {
        Mensagem mensagem = mensagemDAO.findById(idMensagem);

        if (mensagem == null)
            return ApiResponse.error("Mensagem não encontrada.");

        Amizade amizade = amizadeDAO.findById(mensagem.getAmizade());

        if (amizade == null)
            return ApiResponse.error("Amizade não encontrada.");

        if (!remetentePertenceAmizade(usuarioLogado, amizade))
            return ApiResponse.error("Usuário não pertence a essa amizade.");

        if (!mensagem.getRemetente().equals(usuarioLogado))
            return ApiResponse.error("Você só pode deletar mensagens enviadas por você.");

        if (!mensagemDAO.delete(idMensagem))
            return ApiResponse.error("Erro ao deletar mensagem.");

        return ApiResponse.success("Mensagem deletada com sucesso.");
    }

    private boolean remetentePertenceAmizade(Integer remetente, Amizade amizade) {
        return amizade.getUsuario1().equals(remetente)
                || amizade.getUsuario2().equals(remetente);
    }
}
