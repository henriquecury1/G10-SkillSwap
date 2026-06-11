package com.skillswap.service;

import com.skillswap.dao.QualificacaoDAO;
import com.skillswap.dao.SkillDAO;
import com.skillswap.dao.UsuarioDAO;
import com.skillswap.dto.AtualizarPerfilDTO;
import com.skillswap.dto.AtualizarSenhaDTO;
import com.skillswap.dto.CadastroUsuarioDTO;
import com.skillswap.dto.LoginDTO;
import com.skillswap.dto.LoginResponseDTO;
import com.skillswap.dto.PerfilUsuarioDTO;
import com.skillswap.model.Qualificacao;
import com.skillswap.model.Skill;
import com.skillswap.model.Usuario;
import com.skillswap.response.ApiResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;
    private final SkillDAO skillDAO;
    private final QualificacaoDAO qualificacaoDAO;
    private final JWTService jwtService;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        this.skillDAO = new SkillDAO();
        this.qualificacaoDAO = new QualificacaoDAO();
        this.jwtService = new JWTService();
    }

    public ApiResponse<Void> cadastrar(CadastroUsuarioDTO dto) {
        if (dto == null)
            return ApiResponse.error("Dados do usuário não informados.");

        if (isBlank(dto.getNome()))
            return ApiResponse.error("Nome obrigatório.");

        if (isBlank(dto.getEmail()))
            return ApiResponse.error("Email obrigatório.");

        if (isBlank(dto.getSenha()))
            return ApiResponse.error("Senha obrigatória.");

        if (usuarioDAO.findByEmail(dto.getEmail()) != null)
            return ApiResponse.error("Email já cadastrado.");

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setBio(dto.getBio());
        usuario.setNota(null);
        usuario.setNumAvaliacoes(0);

        if (!usuarioDAO.save(usuario))
            return ApiResponse.error("Erro ao cadastrar usuário.");

        return ApiResponse.success("Usuário cadastrado com sucesso.");
    }

    public ApiResponse<LoginResponseDTO> login(LoginDTO dto) {
        if (dto == null || isBlank(dto.getEmail()) || isBlank(dto.getSenha()))
            return ApiResponse.error("Email e senha obrigatórios.");

        Usuario usuario = usuarioDAO.findByEmail(dto.getEmail());

        if (usuario == null || !usuarioDAO.checkPassword(usuario, dto.getSenha()))
            return ApiResponse.error("Email ou senha inválidos.");

        String token = jwtService.generateToken(usuario.getIdUsuario(), usuario.getEmail());

        usuario.setSenha(null);

        LoginResponseDTO data = new LoginResponseDTO(token, usuario);
        return ApiResponse.success("Login realizado com sucesso.", data);
    }

    public Usuario buscarPorId(int idUsuario) {
        Usuario usuario = usuarioDAO.findById(idUsuario);

        if (usuario != null)
            usuario.setSenha(null);

        return usuario;
    }

    public ApiResponse<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioDAO.findAll();

        for (Usuario usuario : usuarios)
            usuario.setSenha(null);

        return ApiResponse.success("Usuários listados com sucesso.", usuarios);
    }

    public ApiResponse<PerfilUsuarioDTO> buscarPerfil(int idUsuario) {
        Usuario usuario = usuarioDAO.findById(idUsuario);

        if (usuario == null)
            return ApiResponse.error("Usuário não encontrado.");

        usuario.setSenha(null);

        List<Skill> skills = listarSkillsCompletasDoUsuario(idUsuario);
        PerfilUsuarioDTO perfil = new PerfilUsuarioDTO(usuario, skills);

        return ApiResponse.success("Perfil encontrado com sucesso.", perfil);
    }

    public ApiResponse<Void> atualizarPerfil(int idUsuario, AtualizarPerfilDTO dto) {
        if (dto == null)
            return ApiResponse.error("Dados do perfil não informados.");

        if (isBlank(dto.getNome()))
            return ApiResponse.error("Nome obrigatório.");

        if (isBlank(dto.getEmail()))
            return ApiResponse.error("Email obrigatório.");

        Usuario atual = usuarioDAO.findById(idUsuario);

        if (atual == null)
            return ApiResponse.error("Usuário não encontrado.");

        Usuario donoEmail = usuarioDAO.findByEmail(dto.getEmail());

        if (donoEmail != null && !donoEmail.getIdUsuario().equals(idUsuario))
            return ApiResponse.error("Email já está em uso por outro usuário.");

        atual.setNome(dto.getNome());
        atual.setEmail(dto.getEmail());
        atual.setBio(dto.getBio());

        if (!usuarioDAO.update(atual))
            return ApiResponse.error("Erro ao atualizar perfil.");

        return ApiResponse.success("Perfil atualizado com sucesso.");
    }

    public ApiResponse<Void> atualizarSenha(int idUsuario, AtualizarSenhaDTO dto) {
        if (dto == null || isBlank(dto.getSenhaAtual()) || isBlank(dto.getNovaSenha()))
            return ApiResponse.error("Senha atual e nova senha são obrigatórias.");

        Usuario usuario = usuarioDAO.findById(idUsuario);

        if (usuario == null)
            return ApiResponse.error("Usuário não encontrado.");

        if (!usuarioDAO.checkPassword(usuario, dto.getSenhaAtual()))
            return ApiResponse.error("Senha atual incorreta.");

        if (!usuarioDAO.updatePassword(idUsuario, dto.getNovaSenha()))
            return ApiResponse.error("Erro ao atualizar senha.");

        return ApiResponse.success("Senha atualizada com sucesso.");
    }

    public boolean adicionarAvaliacaoNaMedia(int idUsuario, float novaNota) {
        if (!notaValida(novaNota))
            return false;

        Usuario usuario = usuarioDAO.findById(idUsuario);

        if (usuario == null)
            return false;

        int numAvaliacoes = usuario.getNumAvaliacoes() != null ? usuario.getNumAvaliacoes() : 0;
        BigDecimal notaAtualBD = usuario.getNota();

        float mediaAtual = notaAtualBD != null ? notaAtualBD.floatValue() : 0;
        int novoNumAvaliacoes = numAvaliacoes + 1;
        float novaMedia = ((mediaAtual * numAvaliacoes) + novaNota) / novoNumAvaliacoes;

        return usuarioDAO.updateNota(idUsuario, novaMedia, novoNumAvaliacoes);
    }

    public boolean editarAvaliacaoNaMedia(int idUsuario, float notaAntiga, float notaNova) {
        if (!notaValida(notaAntiga) || !notaValida(notaNova))
            return false;

        Usuario usuario = usuarioDAO.findById(idUsuario);

        if (usuario == null || usuario.getNumAvaliacoes() == null || usuario.getNumAvaliacoes() <= 0)
            return false;

        int numAvaliacoes = usuario.getNumAvaliacoes();
        float mediaAtual = usuario.getNota() != null ? usuario.getNota().floatValue() : 0;
        float novaMedia = ((mediaAtual * numAvaliacoes) - notaAntiga + notaNova) / numAvaliacoes;

        return usuarioDAO.updateNota(idUsuario, novaMedia, numAvaliacoes);
    }

    private List<Skill> listarSkillsCompletasDoUsuario(int idUsuario) {
        List<Qualificacao> qualificacoes = qualificacaoDAO.findByUsuario(idUsuario);
        List<Skill> skills = new ArrayList<>();

        for (Qualificacao qualificacao : qualificacoes) {
            Skill skill = skillDAO.findById(qualificacao.getSkill());

            if (skill != null)
                skills.add(skill);
        }

        return skills;
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }

    private boolean notaValida(float nota) {
        return nota >= 0 && nota <= 5;
    }
}
