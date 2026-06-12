package com.skillswap.controller;

import com.google.gson.Gson;
import com.skillswap.dto.QualificacaoDTO;
import com.skillswap.response.ApiResponse;
import com.skillswap.service.JWTService;
import com.skillswap.service.QualificacaoService;
import com.skillswap.util.JsonUtil;

import static spark.Spark.*;

public class QualificacaoController {

    private final Gson gson;
    private final JWTService jwtService;
    private final QualificacaoService qualificacaoService;

    public QualificacaoController() {
        this.gson = JsonUtil.createGson();
        this.jwtService = new JWTService();
        this.qualificacaoService = new QualificacaoService();
    }

    public void routes() {

        post("/usuarios/:id/skills", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idUsuario = Integer.parseInt(req.params(":id"));

            if (!idLogado.equals(idUsuario)) {
                res.status(403);
                return gson.toJson(ApiResponse.error("Você só pode alterar suas próprias skills."));
            }

            QualificacaoDTO dto = gson.fromJson(req.body(), QualificacaoDTO.class);

            if (dto == null || dto.getIdSkill() == null) {
                res.status(400);
                return gson.toJson(ApiResponse.error("idSkill obrigatório."));
            }

            ApiResponse<Void> response =
                    qualificacaoService.adicionarSkillAoUsuario(idUsuario, dto.getIdSkill());

            res.status(response.isSuccess() ? 201 : 400);
            return gson.toJson(response);
        });

        get("/usuarios/:id/skills", (req, res) -> {
            res.type("application/json");

            int idUsuario = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = qualificacaoService.listarSkillsDoUsuario(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        get("/usuarios/:id/skills-detalhadas", (req, res) -> {
            res.type("application/json");

            int idUsuario = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = qualificacaoService.listarSkillsDetalhadasDoUsuario(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        get("/skills/:id/usuarios", (req, res) -> {
            res.type("application/json");

            int idSkill = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = qualificacaoService.listarUsuariosPorSkill(idSkill);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        get("/skills/:id/usuarios-detalhados", (req, res) -> {
            res.type("application/json");

            int idSkill = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = qualificacaoService.listarUsuariosDetalhadosPorSkill(idSkill);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        delete("/usuarios/:id/skills/:idSkill", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idUsuario = Integer.parseInt(req.params(":id"));
            int idSkill = Integer.parseInt(req.params(":idSkill"));

            if (!idLogado.equals(idUsuario)) {
                res.status(403);
                return gson.toJson(ApiResponse.error("Você só pode alterar suas próprias skills."));
            }

            ApiResponse<Void> response =
                    qualificacaoService.removerSkillDoUsuario(idUsuario, idSkill);

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
