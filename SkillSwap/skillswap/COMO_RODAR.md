# Como rodar o SkillSwap no GitHub Codespaces

Sem instalar nada na sua máquina além de um navegador.

---

## Passo 1 — Criar uma conta no GitHub

Acesse https://github.com e crie uma conta gratuita caso não tenha.

---

## Passo 2 — Criar um repositório

1. Clique em **New repository** (botão verde no canto superior direito)
2. Dê um nome, ex: `skillswap`
3. Deixe como **Private** se preferir
4. Clique em **Create repository**

---

## Passo 3 — Enviar os arquivos do projeto

Na tela do repositório recém-criado, clique em **uploading an existing file** e envie todos os arquivos do projeto.

Alternativamente, se tiver o Git instalado:

```bash
git init
git remote add origin https://github.com/SEU_USUARIO/skillswap.git
git add .
git commit -m "primeiro commit"
git push -u origin main
```

---

## Passo 4 — Abrir o Codespace

1. No repositório, clique no botão verde **Code**
2. Clique na aba **Codespaces**
3. Clique em **Create codespace on main**

O GitHub vai criar o ambiente e instalar as dependências do frontend automaticamente. Aguarde cerca de 2 minutos na primeira vez.

---

## Passo 5 — Subir o backend

No terminal do Codespace (menu Terminal > New Terminal):

```bash
cd backend
mvn package -DskipTests
java -jar target/skillswap-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Deixe esse terminal rodando.

---

## Passo 6 — Copiar o endereço do backend

1. Clique na aba **Ports** na barra inferior do Codespace
2. Localize a porta **4567**
3. Clique com o botão direito e escolha **Copy Local Address**

O endereço será algo como:
```
https://SEU-CODESPACE-4567.preview.app.github.dev
```

---

## Passo 7 — Configurar o frontend

Crie o arquivo `frontend/.env.local` com o endereço copiado:

```bash
echo "VITE_API_URL=https://SEU-CODESPACE-4567.preview.app.github.dev" > frontend/.env.local
```

Substitua pelo endereço real que você copiou no passo anterior.

---

## Passo 8 — Subir o frontend

Abra um segundo terminal (ícone **+** ao lado do terminal atual):

```bash
cd frontend
npm run dev -- --host
```

---

## Passo 9 — Acessar o projeto

Na aba **Ports**, clique no ícone de globo ao lado da porta **5173** para abrir o projeto no navegador.

---

## Plano gratuito do Codespaces

O GitHub oferece **120 horas/mês** gratuitas no plano free, suficiente para desenvolvimento e apresentação do projeto.
