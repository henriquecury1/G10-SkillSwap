package com.skillswap.controller;

import com.google.gson.Gson;
import com.skillswap.dto.SkillDTO;
import com.skillswap.response.ApiResponse;
import com.skillswap.service.SkillService;
import com.skillswap.util.JsonUtil;

import static spark.Spark.*;

public class SkillController {

    private final Gson gson;
    private final SkillService skillService;

    public SkillController() {
        this.gson = JsonUtil.createGson();
        this.skillService = new SkillService();
    }

    public void routes() {

        post("/skills", (req, res) -> {
            res.type("application/json");

            SkillDTO dto = gson.fromJson(req.body(), SkillDTO.class);
            ApiResponse<Void> response = skillService.cadastrar(dto);

            res.status(response.isSuccess() ? 201 : 400);
            return gson.toJson(response);
        });

        get("/skills", (req, res) -> {
            res.type("application/json");

            ApiResponse<?> response = skillService.listarTodas();

            res.status(200);
            return gson.toJson(response);
        });

        get("/skills/:id", (req, res) -> {
            res.type("application/json");

            int idSkill = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = skillService.buscarPorId(idSkill);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        put("/skills/:id", (req, res) -> {
            res.type("application/json");

            int idSkill = Integer.parseInt(req.params(":id"));
            SkillDTO dto = gson.fromJson(req.body(), SkillDTO.class);

            ApiResponse<Void> response = skillService.atualizar(idSkill, dto);

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        delete("/skills/:id", (req, res) -> {
            res.type("application/json");

            int idSkill = Integer.parseInt(req.params(":id"));
            ApiResponse<Void> response = skillService.deletar(idSkill);

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });
    }
}