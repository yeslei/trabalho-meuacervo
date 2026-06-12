# MeuAcervo

MeuAcervo é uma aplicação para catalogar discos de vinil e CDs. O projeto foi
incrementado a partir de uma versão em Servlets/JSP para uma arquitetura com
API REST em Java e frontend React.

O usuário pode criar conta, buscar álbuns pela API do Discogs, adicionar discos
à coleção ou aos favoritos, avaliar discos com nota de 1 a 5 estrelas e acessar
um perfil com coleção, reviews e favoritos.

## Visão Geral

- **Backend:** Java 21, Jakarta Servlets, Maven, Tomcat 11 e PostgreSQL.
- **Frontend:** React 18, Vite, React Router e Fetch API.
- **Autenticação:** sessão HTTP do Tomcat com cookie `JSESSIONID`.
- **Integração externa:** Discogs API para busca de discos e tracklists.
- **Formato da API:** endpoints REST respondendo JSON.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend | Java 21, Jakarta Servlet 6, Maven |
| Servidor | Apache Tomcat 11 |
| Banco | PostgreSQL 15 |
| Frontend | React 18, Vite 5 |
| Roteamento | React Router |
| JSON | Gson |
| Testes de API | Bruno |
| Senhas | jBCrypt |
| Deploy frontend | GitHub Pages |

## Arquitetura

```text
React SPA
  |
  | Fetch API + credentials: include
  v
Servlets REST
  |
  v
Services
  |
  v
DAOs JDBC
  |
  v
PostgreSQL
```

O backend mantém a separação em camadas da versão original:

```text
Controller -> Service -> DAO -> Model
```

A principal mudança foi na borda da aplicação. Os servlets deixam de fazer
`forward` para JSP ou `redirect` como fluxo principal e passam a responder JSON,
permitindo que o React controle as telas.

## Estrutura

```text
.
├── db/
│   └── schema.sql
├── bruno/
│   ├── bruno.json
│   ├── environments/
│   └── ...
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── context/
│   │   ├── hooks/
│   │   ├── pages/
│   │   ├── routes/
│   │   ├── services/
│   │   └── styles/
│   ├── package.json
│   └── vite.config.js
├── src/main/java/com/seusite/discos/
│   ├── config/
│   ├── controller/
│   ├── dao/
│   ├── db/
│   ├── model/
│   ├── security/
│   ├── service/
│   └── util/
├── src/main/webapp/
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Pré-Requisitos

- Java 21
- Maven 3.8+
- Node.js 18+
- Docker Desktop
- Tomcat 11

## Frontend Hospedado

O frontend React foi configurado para publicacao no GitHub Pages:

```text
https://yeslei.github.io/trabalho-meuacervo/
```

Observacao: o frontend publicado no Pages e estatico. Para login, busca,
colecao e avaliacoes funcionarem, a API Java precisa estar publicada ou
acessivel pela URL configurada em `VITE_API_URL`.

## Como Rodar Localmente

### 1. Subir o banco

Na raiz do projeto:

```bash
docker compose up -d
```

O container cria o banco `site_discos` com usuário `postgres` e senha `123456`.

### 2. Configurar variáveis do frontend

Crie `frontend/.env` a partir do exemplo:

```bash
cp frontend/.env.example frontend/.env
```

Conteúdo esperado para rodar localmente:

```env
VITE_API_URL=http://localhost:8080/backend
```

### 2.1. Configurar variáveis do backend

Crie `.env` na raiz a partir de `.env.example` para rodar localmente, ou cadastre
as mesmas variáveis no provedor onde a API Java ficar hospedada:

```env
DB_JDBC_URL=jdbc:postgresql://localhost:5432/site_discos
DB_USER=postgres
DB_PASSWORD=123456
DISCOGS_TOKEN=seu-token-discogs
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173,https://yeslei.github.io
```

Para Supabase, use a string JDBC do banco com SSL:

```env
DB_JDBC_URL=jdbc:postgresql://HOST:PORT/postgres?sslmode=require
DB_USER=postgres
DB_PASSWORD=SENHA_DO_BANCO
```

No Railway, voce tambem pode cadastrar uma unica variavel `DATABASE_URL` no
formato `postgresql://...`. Se a senha tiver caracteres especiais, prefira usar
`DB_JDBC_URL`, `DB_USER` e `DB_PASSWORD` separados:

```env
DATABASE_URL=postgresql://postgres:SENHA_DO_BANCO@HOST:5432/postgres
```

No GitHub Pages, cadastre `VITE_API_URL` em **Settings > Secrets and variables >
Actions > Variables** com a URL publica da API, por exemplo:

```env
VITE_API_URL=https://sua-api-gratuita.exemplo.com/backend
```

### 3. Buildar o backend

Na raiz do projeto:

```bash
mvn clean package
```

O WAR será gerado em:

```text
target/backend-1.0-SNAPSHOT.war
```

### 4. Publicar no Tomcat

Copie o WAR para o Tomcat com o nome `backend.war`.

Exemplo no Windows:

```powershell
Copy-Item target/backend-1.0-SNAPSHOT.war "C:\Program Files\Apache Software Foundation\Tomcat 11.0\webapps\backend.war" -Force
```

Depois inicie o Tomcat. A API ficará em:

```text
http://localhost:8080/backend
```

### 5. Rodar o frontend

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

Acesse:

```text
http://localhost:5173/trabalho-meuacervo/
```

## Endpoints Principais

| Método | Rota | Descrição |
|---|---|---|
| POST | `/cadastro` | Cria conta e autentica |
| POST | `/login` | Autentica usuário |
| POST | `/logout` | Encerra sessão |
| GET | `/api/me` | Retorna usuário logado |
| GET | `/home` | Lista discos em destaque |
| GET | `/buscar-discos?q=&page=` | Busca discos na API Discogs |
| POST | `/disco/abrir` | Persiste disco e retorna ID interno |
| GET | `/avaliar-disco?id_disco=` | Detalhes, reviews e tracklist |
| POST | `/avaliar-disco` | Cria avaliação |
| PUT | `/avaliar-disco` | Atualiza avaliação |
| GET | `/colecao/ver` | Lista coleção do usuário |
| POST | `/colecao/adicionar` | Adiciona disco à coleção |
| PUT | `/colecao/adicionar` | Atualiza estado e observação privada do item |
| DELETE | `/colecao/adicionar` | Remove disco da coleção |
| GET | `/wishlist/listar` | Lista favoritos |
| POST | `/wishlist/adicionar` | Adiciona favorito |
| DELETE | `/wishlist/adicionar` | Remove favorito |
| GET | `/perfil/reviews` | Lista reviews do usuário |
| GET | `/feed?pagina=` | Lista feed social |
| GET | `/post?id=` | Detalha post |
| POST | `/criar-post` | Cria post |
| POST | `/curtir-post` | Alterna curtida |

Rotas protegidas retornam `401` em JSON quando não há sessão válida:

```json
{
  "erro": "nao-autenticado",
  "mensagem": "Faca login."
}
```

## Testes da API com Bruno

A Collection do Bruno fica na pasta:

```text
bruno/
```

Ela cobre os principais fluxos da API:

- autenticacao: cadastro, login, usuario logado e logout;
- discos: home, busca, abertura, detalhes e atualizacao de avaliacao;
- colecao e favoritos: listagem, adicao, edicao de detalhes e remocao;
- feed: listagem, criacao de post e curtida.

Para executar:

1. Abra o Bruno.
2. Selecione **Open Collection**.
3. Escolha a pasta `bruno/`.
4. Use o ambiente `Local`.
5. Confirme que `baseUrl` esta como `http://localhost:8080/backend`.
6. Rode primeiro `Cadastro` ou `Login` para criar a sessao.

## Deploy do Frontend

O workflow em `.github/workflows/deploy.yml` publica o frontend no GitHub Pages
quando há push na branch `main`.

O Vite usa `base: '/trabalho-meuacervo/'`, portanto a aplicação deve ser aberta
no caminho do repositório quando publicada no Pages.

## Arquivos Locais Ignorados

Não devem ser versionados:

```text
target/
.run-tomcat/
frontend/node_modules/
frontend/dist/
frontend/.env
```

## Observações

- O backend precisa estar em um Tomcat acessível pelo valor de `VITE_API_URL`.
- O frontend enviado ao GitHub Pages é apenas estático; ele não hospeda a API.
- O login local funciona com frontend e backend em `localhost`, pois o navegador
  envia o cookie de sessão entre portas diferentes do mesmo host.
