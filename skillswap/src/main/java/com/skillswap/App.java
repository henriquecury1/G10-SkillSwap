package com.skillswap;

import com.skillswap.controller.AvaliacaoController;
import com.skillswap.controller.MensagemController;
import com.skillswap.controller.AmizadeController;
import com.skillswap.controller.QualificacaoController;
import com.skillswap.controller.SkillController;
import com.skillswap.controller.UsuarioController;
import com.skillswap.service.JWTService;
import com.skillswap.service.AmizadeService;
import com.skillswap.service.QualificacaoService;
import com.skillswap.service.MensagemService;
import com.skillswap.service.SkillService;
import com.skillswap.service.UsuarioService;
import com.skillswap.service.AvaliacaoService;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFiles;

public class App {

    public static void main(String[] args) {

        port(4567);

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            res.type("application/json");
        });

        options("/*", (req, res) -> {
            res.status(200);
            return "OK";
        });

        new UsuarioController().routes();
        new SkillController().routes();
        new QualificacaoController().routes();
        new AmizadeController().routes();
        new MensagemController().routes();
        new AvaliacaoController().routes();

        get("/", (req, res) -> {
            res.type("text/plain");
            return "SkillSwap API rodando";
        });

        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.type("application/json");
            e.printStackTrace();
            res.body("{\"success\":false,\"message\":\"Erro interno no servidor.\"}");
        });

        System.out.println("SkillSwap API rodando em http://localhost:4567");
    }
}
