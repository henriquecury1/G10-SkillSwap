package com.skillswap.controller;

import com.google.gson.Gson;
import com.skillswap.dto.MensagemDTO;
import com.skillswap.response.ApiResponse;
import com.skillswap.service.JWTService;
import com.skillswap.service.MensagemService;
import com.skillswap.util.JsonUtil;

import static spark.Spark.*;

public class MensagemController {

    private final Gson gson;
    private final JWTService jwtService;
    private final MensagemService mensagemService;

    public MensagemController() {
        this.gson = JsonUtil.createGson();
        this.jwtService = new JWTService();
        this.mensagemService = new MensagemService();
    }

    public void routes() {

        post("/amizades/:id/mensagens", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idAmizade = Integer.parseInt(req.params(":id"));
            MensagemDTO dto = gson.fromJson(req.body(), MensagemDTO.class);

            if (dto == null || dto.getConteudo() == null || dto.getConteudo().isBlank()) {
                res.status(400);
                return gson.toJson(ApiResponse.error("Conteúdo da mensagem obrigatório."));
            }

            ApiResponse<Void> response =
                    mensagemService.enviarMensagemTexto(idAmizade, idLogado, dto.getConteudo());

            res.status(response.isSuccess() ? 201 : 400);
            return gson.toJson(response);
        });

        get("/amizades/:id/mensagens", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idAmizade = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = mensagemService.listarMensagensDaAmizade(idAmizade, idLogado);

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        delete("/mensagens/:id", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idMensagem = Integer.parseInt(req.params(":id"));
            ApiResponse<Void> response = mensagemService.deletarMensagem(idMensagem, idLogado);

            res.status(response.isSuccess() ? 200 : 400);
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