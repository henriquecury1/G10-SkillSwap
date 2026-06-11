# Como rodar o SkillSwap no GitHub Codespaces

Sem instalar nada na sua máquina além de um navegador.

---

## Passo 1 — Abrir o Codespace

1. Acesse o repositório no GitHub
2. Clique no botão verde **Code**
3. Clique na aba **Codespaces**
4. Clique em **Create codespace on main** (ou abra um já existente)

Aguarde o ambiente subir. Na primeira vez leva cerca de 2 minutos.

---

## Passo 2 — Subir o backend

No terminal, cole tudo de uma vez:

```bash
cd /workspaces/G10-SkillSwap/SkillSwap/skillswap/backend
source /usr/local/sdkman/bin/sdkman-init.sh
export JAVA_HOME=/usr/local/sdkman/candidates/java/17.0.11-ms
export PATH=$JAVA_HOME/bin:$PATH
mvn package -DskipTests
java -jar target/skillswap-0.0.1-SNAPSHOT-exec.jar
```

Aguarde até aparecer: SkillSwap API rodando em http://localhost:4567

Deixe esse terminal rodando.

---

## Passo 3 — Copiar o endereço do backend

1. Clique na aba **Ports** na barra inferior do Codespace
2. Localize a porta **4567**
3. Clique com o botão direito e escolha **Copy Local Address**

O endereço será algo como: https://seu-codespace-4567.app.github.dev/

---

## Passo 4 — Subir o frontend

Abra um **segundo terminal** (ícone **+** ao lado do terminal atual) e cole:

```bash
cd /workspaces/G10-SkillSwap/SkillSwap/skillswap/frontend
echo "VITE_API_URL=https://SEU-CODESPACE-4567.app.github.dev" > .env.local
npm install
npm run dev -- --host
```

Substitua o endereço pelo que você copiou no passo anterior.

---

## Passo 5 — Acessar o projeto

Na aba **Ports**, clique no ícone de globo ao lado da porta **5173** para abrir o SkillSwap no navegador.

---

## Observações

- Os passos 2, 3 e 4 precisam ser repetidos toda vez que abrir o Codespace, pois as variáveis de ambiente não persistem entre sessões.
- O endereço da porta 4567 muda a cada sessão, então o `.env.local` precisa ser atualizado sempre.
- O plano gratuito do GitHub oferece **120 horas/mês** de Codespaces.