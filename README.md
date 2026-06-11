# SkillSwap

O objetivo do projeto SkillSwap é desenvolver uma plataforma que conecta estudantes universitários com base em suas habilidades, interesses e disponibilidade, solucionando a dificuldade que encontram para achar colegas ideais para aprender, ensinar ou formar grupos de estudo. Para isso, a proposta incentiva a colaboração mútua e a troca de conhecimento através de um ecossistema que traz como diferencial inteligente a realização de videochamadas com tradução e legenda automáticas, ampliando a acessibilidade, a comunicação e a integração entre os estudantes.

## Alunos integrantes da equipe

* Henrique de Oliveira Rettore Cury
* Igor Eduardo Costa Silva
* Lucas Mendes Carvalho
* Vitor Melo Costa

## Professores responsáveis

* Wladmir Cardoso Brandao
* Sandro Jerônimo de Almeida  

## Instruções de utilização

# SkillSwap

Plataforma universitária de troca de conhecimentos entre estudantes.

## Estrutura

```
skillswap/
├── backend/       # Java + Spark Framework (porta 4567)
├── frontend/      # React + TypeScript + Vite (porta 5173)
└── README.md
```

## Como rodar

### Backend

```bash
cd backend
mvn package -DskipTests
java -jar target/skillswap-1.0-SNAPSHOT-jar-with-dependencies.jar
```

O backend sobe em `http://localhost:4567`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend sobe em `http://localhost:5173`.

O Vite já está configurado para fazer proxy das requisições `/api/*` para o backend em `localhost:4567`, então não há problema de CORS no desenvolvimento.

## Stack

**Backend:** Java, Spark Framework, JWT, Gson, H2/MySQL

**Frontend:** React 18, TypeScript, Vite, Tailwind CSS, React Router v6, Axios, TanStack Query v5, React Hook Form, Zod, Lucide React

## Endpoints da API

| Método | Rota | Autenticação | Descrição |
|--------|------|-------------|-----------|
| POST | /usuarios | Não | Cadastro |
| POST | /login | Não | Login (retorna JWT) |
| GET | /usuarios/:id | Não | Buscar usuário |
| PUT | /usuarios/:id | Sim | Atualizar perfil |
| PUT | /usuarios/:id/senha | Sim | Alterar senha |
| GET | /skills | Não | Listar skills |
| POST | /skills | Não | Cadastrar skill |
| PUT | /skills/:id | Não | Atualizar skill |
| DELETE | /skills/:id | Não | Deletar skill |
| GET | /usuarios/:id/skills | Não | Skills do usuário |
| POST | /usuarios/:id/skills | Sim | Adicionar skill ao perfil |
| DELETE | /usuarios/:id/skills/:idSkill | Sim | Remover skill do perfil |
| POST | /amizades/solicitacoes | Sim | Enviar solicitação |
| PUT | /amizades/:id/aceitar | Sim | Aceitar solicitação |
| PUT | /amizades/:id/recusar | Sim | Recusar solicitação |
| DELETE | /amizades/:id | Sim | Remover amizade |
| GET | /usuarios/:id/amizades | Não | Listar amizades |
| GET | /usuarios/:id/amizades/recebidas | Sim | Solicitações recebidas |
| GET | /usuarios/:id/amizades/enviadas | Sim | Solicitações enviadas |
| POST | /amizades/:id/mensagens | Sim | Enviar mensagem |
| GET | /amizades/:id/mensagens | Sim | Listar mensagens |
| DELETE | /mensagens/:id | Sim | Deletar mensagem |
| POST | /avaliacoes | Sim | Avaliar usuário |
| PUT | /avaliacoes | Sim | Editar avaliação |
| GET | /usuarios/:id/avaliacoes | Não | Listar avaliações |

