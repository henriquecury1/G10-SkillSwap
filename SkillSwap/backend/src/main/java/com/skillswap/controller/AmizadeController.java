package com.skillswap.controller;

import com.google.gson.Gson;
import com.skillswap.dto.BloqueioDTO;
import com.skillswap.dto.SolicitacaoAmizadeDTO;
import com.skillswap.response.ApiResponse;
import com.skillswap.service.AmizadeService;
import com.skillswap.service.JWTService;
import com.skillswap.util.JsonUtil;

import static spark.Spark.*;

public class AmizadeController {

    private final Gson gson;
    private final JWTService jwtService;
    private final AmizadeService amizadeService;

    public AmizadeController() {
        this.gson = JsonUtil.createGson();
        this.jwtService = new JWTService();
        this.amizadeService = new AmizadeService();
    }

    public void routes() {

        post("/amizades/solicitacoes", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            SolicitacaoAmizadeDTO dto = gson.fromJson(req.body(), SolicitacaoAmizadeDTO.class);

            if (dto == null || dto.getIdDestinatario() == null) {
                res.status(400);
                return gson.toJson(ApiResponse.error("idDestinatario obrigatório."));
            }

            ApiResponse<Void> response =
                    amizadeService.enviarSolicitacao(idLogado, dto.getIdDestinatario());

            res.status(response.isSuccess() ? 201 : 400);
            return gson.toJson(response);
        });

        put("/amizades/:id/aceitar", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idAmizade = Integer.parseInt(req.params(":id"));
            ApiResponse<Void> response = amizadeService.aceitarSolicitacao(idAmizade, idLogado);

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        put("/amizades/:id/recusar", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idAmizade = Integer.parseInt(req.params(":id"));
            ApiResponse<Void> response = amizadeService.recusarSolicitacao(idAmizade, idLogado);

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        post("/amizades/bloqueios", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            BloqueioDTO dto = gson.fromJson(req.body(), BloqueioDTO.class);

            if (dto == null || dto.getIdBloqueado() == null) {
                res.status(400);
                return gson.toJson(ApiResponse.error("idBloqueado obrigatório."));
            }

            ApiResponse<Void> response =
                    amizadeService.bloquearUsuario(idLogado, dto.getIdBloqueado());

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        delete("/amizades/bloqueios/:idBloqueado", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idBloqueado = Integer.parseInt(req.params(":idBloqueado"));
            ApiResponse<Void> response = amizadeService.desbloquearUsuario(idLogado, idBloqueado);

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        delete("/amizades/:id", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idAmizade = Integer.parseInt(req.params(":id"));
            ApiResponse<Void> response = amizadeService.removerAmizade(idAmizade, idLogado);

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        get("/usuarios/:id/amizades", (req, res) -> {
            res.type("application/json");

            int idUsuario = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = amizadeService.listarAmizadesDoUsuario(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        get("/usuarios/:id/amizades-detalhadas", (req, res) -> {
            res.type("application/json");

            int idUsuario = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = amizadeService.listarAmizadesDetalhadasDoUsuario(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        get("/usuarios/:id/amizades/recebidas", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idUsuario = Integer.parseInt(req.params(":id"));

            if (!idLogado.equals(idUsuario)) {
                res.status(403);
                return gson.toJson(ApiResponse.error("Você só pode ver suas próprias solicitações."));
            }

            ApiResponse<?> response = amizadeService.listarSolicitacoesRecebidas(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        get("/usuarios/:id/amizades/recebidas-detalhadas", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idUsuario = Integer.parseInt(req.params(":id"));

            if (!idLogado.equals(idUsuario)) {
                res.status(403);
                return gson.toJson(ApiResponse.error("Você só pode ver suas próprias solicitações."));
            }

            ApiResponse<?> response = amizadeService.listarSolicitacoesRecebidasDetalhadas(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        get("/usuarios/:id/amizades/enviadas", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idUsuario = Integer.parseInt(req.params(":id"));

            if (!idLogado.equals(idUsuario)) {
                res.status(403);
                return gson.toJson(ApiResponse.error("Você só pode ver suas próprias solicitações."));
            }

            ApiResponse<?> response = amizadeService.listarSolicitacoesEnviadas(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        get("/usuarios/:id/amizades/enviadas-detalhadas", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idUsuario = Integer.parseInt(req.params(":id"));

            if (!idLogado.equals(idUsuario)) {
                res.status(403);
                return gson.toJson(ApiResponse.error("Você só pode ver suas próprias solicitações."));
            }

            ApiResponse<?> response = amizadeService.listarSolicitacoesEnviadasDetalhadas(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });
    }

    private Integer getIdUsuarioAutenticado(String authorizationHeader) {
        try {
            String token = jwtService.extractToken(authorizationHeader);
            return token != null ? jwtService.getIdUsuarioFromToken(token) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
