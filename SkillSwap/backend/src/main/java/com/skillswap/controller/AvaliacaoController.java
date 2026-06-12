package com.skillswap.controller;

import com.google.gson.Gson;
import com.skillswap.dto.AvaliacaoDTO;
import com.skillswap.response.ApiResponse;
import com.skillswap.service.AvaliacaoService;
import com.skillswap.service.JWTService;
import com.skillswap.util.JsonUtil;

import static spark.Spark.*;

public class AvaliacaoController {

    private final Gson gson;
    private final JWTService jwtService;
    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController() {
        this.gson = JsonUtil.createGson();
        this.jwtService = new JWTService();
        this.avaliacaoService = new AvaliacaoService();
    }

    public void routes() {

        post("/avaliacoes", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            AvaliacaoDTO dto = gson.fromJson(req.body(), AvaliacaoDTO.class);

            if (dto == null || dto.getIdAvaliado() == null || dto.getNota() == null) {
                res.status(400);
                return gson.toJson(ApiResponse.error("idAvaliado e nota são obrigatórios."));
            }

            ApiResponse<Void> response =
                    avaliacaoService.avaliarUsuario(idLogado, dto.getIdAvaliado(), dto.getNota());

            res.status(response.isSuccess() ? 201 : 400);
            return gson.toJson(response);
        });

        put("/avaliacoes", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            AvaliacaoDTO dto = gson.fromJson(req.body(), AvaliacaoDTO.class);

            if (dto == null || dto.getIdAvaliado() == null || dto.getNota() == null) {
                res.status(400);
                return gson.toJson(ApiResponse.error("idAvaliado e nota são obrigatórios."));
            }

            ApiResponse<Void> response =
                    avaliacaoService.editarAvaliacao(idLogado, dto.getIdAvaliado(), dto.getNota());

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        get("/usuarios/:id/avaliacoes", (req, res) -> {
            res.type("application/json");

            int idUsuario = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = avaliacaoService.listarAvaliacoesRecebidas(idUsuario);

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