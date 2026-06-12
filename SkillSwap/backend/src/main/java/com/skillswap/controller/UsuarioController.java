package com.skillswap.controller;

import com.google.gson.Gson;
import com.skillswap.dto.AtualizarPerfilDTO;
import com.skillswap.dto.AtualizarSenhaDTO;
import com.skillswap.dto.CadastroUsuarioDTO;
import com.skillswap.dto.LoginDTO;
import com.skillswap.model.Usuario;
import com.skillswap.response.ApiResponse;
import com.skillswap.service.JWTService;
import com.skillswap.service.UsuarioService;
import com.skillswap.util.JsonUtil;

import static spark.Spark.*;

public class UsuarioController {

    private final Gson gson;
    private final UsuarioService usuarioService;
    private final JWTService jwtService;

    public UsuarioController() {
        this.gson = JsonUtil.createGson();
        this.usuarioService = new UsuarioService();
        this.jwtService = new JWTService();
    }

    public void routes() {

        post("/usuarios", (req, res) -> {
            res.type("application/json");

            CadastroUsuarioDTO dto = gson.fromJson(req.body(), CadastroUsuarioDTO.class);
            ApiResponse<Void> response = usuarioService.cadastrar(dto);

            res.status(response.isSuccess() ? 201 : 400);
            return gson.toJson(response);
        });

        post("/login", (req, res) -> {
            res.type("application/json");

            LoginDTO dto = gson.fromJson(req.body(), LoginDTO.class);
            ApiResponse<?> response = usuarioService.login(dto);

            res.status(response.isSuccess() ? 200 : 401);
            return gson.toJson(response);
        });

        get("/usuarios", (req, res) -> {
            res.type("application/json");

            ApiResponse<?> response = usuarioService.listarTodos();

            res.status(200);
            return gson.toJson(response);
        });

        get("/usuarios/:id", (req, res) -> {
            res.type("application/json");

            int idUsuario = Integer.parseInt(req.params(":id"));
            Usuario usuario = usuarioService.buscarPorId(idUsuario);

            if (usuario == null) {
                res.status(404);
                return gson.toJson(ApiResponse.error("Usuário não encontrado."));
            }

            return gson.toJson(ApiResponse.success("Usuário encontrado.", usuario));
        });

        get("/usuarios/:id/perfil", (req, res) -> {
            res.type("application/json");

            int idUsuario = Integer.parseInt(req.params(":id"));
            ApiResponse<?> response = usuarioService.buscarPerfil(idUsuario);

            res.status(response.isSuccess() ? 200 : 404);
            return gson.toJson(response);
        });

        put("/usuarios/:id", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idUsuario = Integer.parseInt(req.params(":id"));

            if (!idLogado.equals(idUsuario)) {
                res.status(403);
                return gson.toJson(ApiResponse.error("Você só pode editar seu próprio perfil."));
            }

            AtualizarPerfilDTO dto = gson.fromJson(req.body(), AtualizarPerfilDTO.class);
            ApiResponse<Void> response = usuarioService.atualizarPerfil(idUsuario, dto);

            res.status(response.isSuccess() ? 200 : 400);
            return gson.toJson(response);
        });

        put("/usuarios/:id/senha", (req, res) -> {
            res.type("application/json");

            Integer idLogado = getIdUsuarioAutenticado(req.headers("Authorization"));

            if (idLogado == null) {
                res.status(401);
                return gson.toJson(ApiResponse.error("Token inválido ou ausente."));
            }

            int idUsuario = Integer.parseInt(req.params(":id"));

            if (!idLogado.equals(idUsuario)) {
                res.status(403);
                return gson.toJson(ApiResponse.error("Você só pode alterar sua própria senha."));
            }

            AtualizarSenhaDTO dto = gson.fromJson(req.body(), AtualizarSenhaDTO.class);
            ApiResponse<Void> response = usuarioService.atualizarSenha(idUsuario, dto);

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
