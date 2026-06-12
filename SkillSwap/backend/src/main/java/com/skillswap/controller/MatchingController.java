package com.skillswap.controller;

import com.google.gson.Gson;
import com.skillswap.dto.MatchingRequestDTO;
import com.skillswap.dto.MatchingResponseDTO;
import com.skillswap.response.ApiResponse;
import com.skillswap.service.JWTService;
import com.skillswap.service.MatchingService;
import com.skillswap.util.JsonUtil;

import java.util.List;

import static spark.Spark.post;

public class MatchingController {

    private final Gson gson;
    private final MatchingService matchingService;
    private final JWTService jwtService;

    public MatchingController() {
        this.gson = JsonUtil.createGson();
        this.matchingService = new MatchingService();
        this.jwtService = new JWTService();
    }

    public void routes() {

        post("/matching", (req, res) -> {
            res.type("application/json");

            // autenticação JWT obrigatória
            Integer idUsuario = getIdUsuarioAutenticado(req.headers("Authorization"));
            if (idUsuario == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            MatchingRequestDTO requestDTO = gson.fromJson(req.body(), MatchingRequestDTO.class);

            List<String> horarios = requestDTO != null && requestDTO.getHorarios() != null
                    ? requestDTO.getHorarios()
                    : List.of();

            if (horarios.isEmpty()) {
                res.status(400);
                return gson.toJson(ApiResponse.error("Informe ao menos um horário disponível."));
            }

            MatchingResponseDTO result = matchingService.gerarMatches(idUsuario, horarios);

            return gson.toJson(ApiResponse.success("Matches gerados com sucesso.", result));
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
