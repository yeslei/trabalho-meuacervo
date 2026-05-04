# MeuAcervo

Aplicação web para catalogar coleções de discos de vinil e CDs. O usuário cria uma conta, pesquisa álbuns via API do Discogs, adiciona títulos à sua coleção ou lista de desejos, escreve reviews com nota de 1 a 5 estrelas e acompanha o perfil com estatísticas.

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Runtime | Jakarta EE 6 — Tomcat 10.1+ / **Tomcat 11** |
| Build | Maven 3 (empacota `.war`) |
| Banco de dados | PostgreSQL 15 |
| Container do banco | Docker + Docker Compose |
| Views | JSP + JSTL 3 |
| API externa | Discogs REST API |
| Serialização JSON | GSON 2.10 |
| Hash de senha | jBCrypt 0.4 |

---

## Arquitetura

O projeto segue o padrão **MVC clássico com Servlets Jakarta EE**.

```
Browser
  │
  ▼
[ Filters ]
  ├── EncodingFilter (@WebFilter "/*")  → UTF-8 em toda requisição/resposta
  └── AuthFilter    (@WebFilter rotas protegidas) → bloqueia sem sessão ou cookie válido
  │
  ▼
[ Controllers — Servlets ]
  │   um Servlet por caso de uso  (@WebServlet)
  │   lê parâmetros, chama Service, redireciona (PRG) ou faz forward para JSP
  ▼
[ Services ]
  │   orquestram DAOs e chamadas externas
  │   contêm a lógica de negócio (validações, decisões)
  ▼
[ DAOs ]                          [ DiscogsService ]
  │   SQL puro via JDBC               Java HttpClient nativo
  │   um DAO por entidade             GSON para parse do JSON
  ▼
PostgreSQL                        Discogs REST API
```

### Estrutura de pacotes

```
com.seusite.discos
├── config/       ConnectionFactory (JDBC), ApiConfig (leitura do token Discogs)
├── controller/   Servlets — um por caso de uso
├── dao/          Acesso ao banco — uma classe por entidade
├── db/           DatabaseInitializer + AppInitListener (cria tabelas no boot)
├── model/        POJOs: Usuario, Disco, Colecao, AvaliacaoDisco, Wishlist, Post…
├── security/     AuthFilter (sessão + cookie) e EncodingFilter
├── service/      Lógica de negócio que orquestra DAOs e APIs externas
└── util/         ValidadorUtil, SenhaUtil
```

### Por que Servlets puros e não Spring?

Decisão do projeto: demonstrar a camada de Servlet/Filter do Jakarta EE sem abstrações de framework. Cada passo — parsing de parâmetro, gerenciamento de sessão, redirecionamento PRG — é explícito e rastreável no código.

---

## Banco de dados

O schema é criado automaticamente no primeiro boot pelo `DatabaseInitializer`, invocado pelo `AppInitListener` (`@WebListener`). **Não é necessário rodar nenhum script SQL manualmente.**

### Diagrama de entidades

```
usuario ──────────────────────────────────────────────┐
  │                                                    │
  ├── colecao (1:1)                                    │
  │     └── item_colecao (N:N com disco)               │
  │                                                    │
  ├── wishlist (N:N com disco)                         │
  │                                                    │
  ├── avaliacao_disco (N:N com disco, unique)          │
  │                                                    │
  └── post ──── curtida ───────────────────────────────┘
                  │
                disco
```

### Entidades

| Tabela | Descrição |
|---|---|
| `usuario` | Conta do usuário (email, senha bcrypt, username) |
| `disco` | Catálogo local — espelho do Discogs |
| `colecao` | Coleção principal de cada usuário (1:1) |
| `item_colecao` | Disco dentro de uma coleção (estado de conservação, observação) |
| `wishlist` | Lista de desejos (usuario × disco) |
| `avaliacao_disco` | Review com nota 1–5 e comentário (único por usuario × disco) |
| `post` | Publicações no feed social |
| `curtida` | Curtidas em posts |

---

## Lógica de negócio

### Autenticação e sessão

- Cadastro valida formato de e-mail, unicidade de username e senha mínima de 8 caracteres
- Senha armazenada como hash BCrypt com salt automático — nunca em texto puro
- Login cria uma `HttpSession` com duração de 30 minutos
- Com "Manter conectado" marcado, um cookie `usuarioId` é gravado por 7 dias
- O `AuthFilter` lê esse cookie e reconstrói a sessão via lookup no banco, sem pedir login novamente
- Ao criar conta, o usuário já é autenticado automaticamente e redirecionado para `/home`

### Disco como entidade local

O Discogs é apenas fonte de consulta — os dados ficam no banco próprio. Ao salvar qualquer interação (coleção, wishlist, avaliação), `DiscoService.obterOuSalvarDisco()` verifica se o disco já existe pelo `discogs_id`. Se não existir, persiste antes de criar a relação. Isso garante integridade referencial e evita duplicatas.

### Busca e detalhes

- `BuscarDiscosServlet` envia o termo para `DiscogsService`, que consulta a API e retorna até 50 resultados por página
- O título vem no formato `"Artista - Álbum"` e é separado pelo service antes de popular o `Disco`
- `DiscoAbrirServlet` (`/disco/abrir`) recebe o `discogsId`, persiste o disco localmente e redireciona para `/avaliar-disco` com o ID interno
- O tracklist é carregado via **AJAX** (`/ver-tracklist?id=<discogsId>`) para não bloquear o carregamento inicial da página

### Coleção e wishlist

- Cada usuário tem exatamente **uma coleção principal**, criada automaticamente no primeiro acesso
- Adicionar e remover usam o mesmo endpoint com parâmetro `acao=adicionar|remover`
- Todo POST bem-sucedido termina com `sendRedirect` (padrão **PRG**), evitando resubmissão ao pressionar F5

### Perfil

- Três abas: **Coleção** (`/colecao/ver`), **Reviews** (`/perfil/reviews`), **Favoritos** (`/wishlist/listar`)
- Os três contadores no topo (discos, reviews, favoritos) sempre buscam o valor real do banco, independente de qual aba está ativa

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java JDK | 21 (`java -version`) |
| Maven | 3.8 (`mvn -version`) |
| Docker + Docker Compose | qualquer (`docker -v`) |
| Apache Tomcat | **11** (ou 10.1) |

> **Tomcat 9 não funciona.** O projeto usa o namespace `jakarta.*` (Jakarta EE 9+). Tomcat 9 ainda usa `javax.*` e lançará erros de `ClassNotFoundException` no deploy.

---

## Como rodar (passo a passo)

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd trabalho-meuacervo
```

### 2. Suba o banco de dados

```bash
docker compose up -d
```

PostgreSQL 15 vai subir na porta `5432`. O banco `site_discos` e todas as tabelas são criadas automaticamente — nenhum script SQL adicional é necessário.

### 3. Compile e gere o WAR

```bash
mvn clean package
```

O arquivo estará em `target/backend-1.0-SNAPSHOT.war`.

### 4. Deploy no Tomcat 11

```bash
# Substitua ~/tomcat11 pelo caminho da sua instalação
cp target/backend-1.0-SNAPSHOT.war ~/tomcat11/webapps/backend.war
~/tomcat11/bin/startup.sh
```

### 5. Acesse

```
http://localhost:8080/backend/login.jsp
```

Crie uma conta e use normalmente. Após o cadastro o sistema já faz o login automaticamente.

---

## Configuração

### Banco de dados

Os valores padrão estão em `ConnectionFactory.java` e já batem com o `docker-compose.yml`:

| Parâmetro | Valor padrão |
|---|---|
| Host | `localhost:5432` |
| Banco | `site_discos` |
| Usuário | `postgres` |
| Senha | `123456` |

Para alterar, edite `src/main/java/com/seusite/discos/config/ConnectionFactory.java`.

### Token da API do Discogs

O token de exemplo já está em `config.properties`. Para usar o seu próprio (recomendado):

```properties
# src/main/java/com/seusite/discos/config/config.properties
discogs.api.url=https://api.discogs.com
discogs.api.token=SEU_TOKEN_AQUI
```

Ou via variável de ambiente (tem precedência sobre o arquivo):

```bash
export DISCOGS_API_TOKEN=SEU_TOKEN_AQUI
```

---

## Rotas

| Método | Rota | Descrição | Protegida |
|---|---|---|---|
| GET | `/login.jsp` | Tela de login | — |
| POST | `/login` | Autentica e cria sessão | — |
| GET | `/cadastro.jsp` | Tela de cadastro | — |
| POST | `/cadastro` | Cria conta e faz login automático | — |
| GET | `/home` | Home com discos em destaque | sim |
| GET | `/buscar-discos?q=termo` | Busca na API do Discogs | sim |
| GET | `/disco/abrir?idDisco=X` | Persiste disco e redireciona para detalhes | sim |
| GET | `/avaliar-disco?id_disco=X` | Página de detalhes, tracklist e reviews | sim |
| POST | `/avaliar-disco` | Salva review com nota | sim |
| POST | `/colecao/adicionar` | Adiciona ou remove da coleção | sim |
| POST | `/wishlist/adicionar` | Adiciona ou remove da wishlist | sim |
| GET | `/colecao/ver` | Perfil — aba Coleção | sim |
| GET | `/perfil/reviews` | Perfil — aba Reviews | sim |
| GET | `/wishlist/listar` | Perfil — aba Favoritos | sim |
| GET | `/ver-tracklist?id=X` | Tracklist via AJAX (retorna HTML parcial) | sim |
| GET | `/logout` | Encerra sessão e limpa cookies | sim |

---

## Decisões técnicas

**BCrypt com salt automático** — `BCrypt.gensalt()` gera um salt diferente a cada cadastro. Senhas iguais produzem hashes diferentes, o que protege contra rainbow table attacks.

**PRG (Post/Redirect/Get)** — Todo POST bem-sucedido termina com `sendRedirect`. Evita o problema clássico de reenvio de formulário ao pressionar F5 após uma ação.

**`AppInitListener` + `DatabaseInitializer`** — O `@WebListener` invoca `CREATE TABLE IF NOT EXISTS` na inicialização do contexto. Idempotente: rodar múltiplas vezes não tem efeito colateral. Nenhum setup manual de banco necessário.

**Session + Cookie "manter conectado"** — Sessão padrão de 30 minutos. Com a opção marcada, cookie `usuarioId` gravado por 7 dias. `AuthFilter` reconstrói a sessão a partir do cookie, sem expor senha.

**`HttpClient` nativo do Java 11+** — Sem dependência adicional de biblioteca HTTP. O header `User-Agent` é obrigatório na API do Discogs; sem ele as requisições são bloqueadas com 403.

**`<c:out>`** em todo output dinâmico nas JSPs — Escapa automaticamente caracteres HTML (`<`, `>`, `"`, `&`), prevenindo XSS sem lógica adicional no servidor.

---

---

## Script copia-e-cola para colegas

Salve o conteúdo abaixo como `setup.sh` na raiz do projeto e execute:

```bash
chmod +x setup.sh && ./setup.sh
```

```bash
#!/bin/bash
# setup.sh — sobe banco, compila, faz deploy e abre o navegador

set -e

TOMCAT_DIR="$HOME/tomcat11"
TOMCAT_URL="https://downloads.apache.org/tomcat/tomcat-11/v11.0.21/bin/apache-tomcat-11.0.21.tar.gz"
WAR_NAME="backend-1.0-SNAPSHOT.war"
APP_URL="http://localhost:8080/backend/login.jsp"

echo "==> 1/5  Subindo PostgreSQL via Docker..."
docker compose up -d
echo "      aguardando banco ficar pronto..."
until docker exec postgres-discos pg_isready -q; do sleep 1; done
echo "      banco OK"

echo "==> 2/5  Compilando projeto com Maven..."
mvn clean package -q
echo "      WAR gerado: target/$WAR_NAME"

echo "==> 3/5  Verificando Tomcat 11..."
if [ ! -d "$TOMCAT_DIR" ]; then
  echo "      Tomcat não encontrado — baixando..."
  curl -L "$TOMCAT_URL" -o /tmp/tomcat11.tar.gz
  mkdir -p "$TOMCAT_DIR"
  tar -xzf /tmp/tomcat11.tar.gz -C "$TOMCAT_DIR" --strip-components=1
  chmod +x "$TOMCAT_DIR/bin/"*.sh
  echo "      Tomcat instalado em $TOMCAT_DIR"
fi

echo "==> 4/5  Parando Tomcat anterior (se houver)..."
"$TOMCAT_DIR/bin/shutdown.sh" 2>/dev/null || true
sleep 2
pkill -f "catalina" 2>/dev/null || true
sleep 1

echo "==> 5/5  Fazendo deploy e iniciando Tomcat..."
cp "target/$WAR_NAME" "$TOMCAT_DIR/webapps/backend.war"
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
"$TOMCAT_DIR/bin/startup.sh"

echo ""
echo "      aguardando aplicação subir..."
for i in $(seq 1 30); do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL" 2>/dev/null || echo "000")
  if [ "$CODE" = "200" ]; then
    echo ""
    echo "======================================================"
    echo "  Aplicação disponível em: $APP_URL"
    echo "======================================================"
    # tenta abrir no navegador (funciona no Linux com interface gráfica)
    xdg-open "$APP_URL" 2>/dev/null || true
    exit 0
  fi
  printf "."
  sleep 2
done

echo ""
echo "  Tomcat demorou mais que o esperado. Verifique os logs:"
echo "  tail -f $TOMCAT_DIR/logs/catalina.out"
```

---

---

## Enunciado original do projeto

### Objetivo

Desenvolver um sistema Web completo e responsivo, aplicando os conceitos fundamentais de desenvolvimento **Front-end** e **Back-end**, sob a arquitetura **MVC (Model-View-Controller)**, garantindo separação entre lógica de negócios, controle de requisições e interface com o usuário.

### Tecnologias exigidas

**Front-end:** HTML5, CSS3, JavaScript

**Back-end:** Java, Servlets, JSP

### Armazenamento de dados

- **Banco de dados relacional** — persistência definitiva
- **Sessões (`HttpSession`)** — usuário logado, dados temporários
- **Cookies** — persistência mesmo após fechar o browser
- **Cache HTTP** — headers `Cache-Control` / `Expires`

### Segurança

- Validação de formulários com JavaScript (front-end)
- Autenticação e autorização com verificação de sessão ativa nos Servlets (back-end)
- Proteção de páginas JSP

### Controle de versão

Uso obrigatório de GitHub com branches, commits claros e Pull Requests.

### Avaliação

- **Individual:** baseada nos commits e Pull Requests
- **Coletiva:** funcionamento geral do sistema
