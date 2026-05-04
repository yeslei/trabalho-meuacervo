# MeuAcervo — Patch para a branch `bruno-dev`

Este pacote contém **28 arquivos** (10 servlets, 5 DAOs, 1 service, 1 filter, 1 listener, 8 JSPs, 1 CSS, 1 web.xml) para corrigir 100% dos bugs reportados e implementar as 4 telas dos prints do Figma.

Toda a stack continua **Tomcat 9 + javax.servlet + JSTL 1.2 + JDK 21**, conforme o pom.xml original. Nenhum scriptlet `<% %>`. Nenhum `jakarta`. JSTL puro com `${...}`.

---

## 1) Como aplicar o patch

A estrutura abaixo espelha exatamente a da branch `bruno-dev`. Basta **descompactar este zip na raiz do repositório** que os arquivos certos serão sobrescritos / adicionados:

```
trabalho-meuacervo/
├── backend/
│   └── src/main/java/com/seusite/discos/
│       ├── controller/   ← 10 servlets (4 modificados, 6 NOVOS)
│       ├── dao/          ← 5 DAOs (2 modificados, 3 NOVOS)
│       ├── service/      ← 1 modificado
│       ├── security/     ← 1 modificado
│       └── listener/     ← NOVO pacote
└── src/main/webapp/
    ├── WEB-INF/web.xml   ← atualizado
    ├── assets/css/style.css  ← NOVO (todas as variáveis CSS dos JSPs vivem aqui)
    └── *.jsp             ← 4 modificados, 4 NOVOS
```

Depois é só rodar `mvn clean package` e re-subir o Tomcat.

> **Validação que rodei aqui:**
> - `javac --release 21` em todos os 34 .java do projeto: 0 erros, 0 warnings
> - `JspC` (Tomcat 9 Jasper) em todos os 8 JSPs: 0 erros
> - WAR foi deployado pelo Tomcat 9 sem erros de classpath/listener

---

## 2) Mapa de URLs (todas casam com os `href` e `action` dos JSPs)

| Rota                                 | Método      | Servlet                    | Função                                                    |
|--------------------------------------|-------------|----------------------------|-----------------------------------------------------------|
| `/listarFeedServlet`                 | GET         | ListarFeedServlet          | Home com 3 seções de discos (print 4)                     |
| `/loginServlet`                      | GET / POST  | LoginServlet               | Login (GET redireciona para login.jsp)                    |
| `/cadastroServlet`                   | GET / POST  | CadastroServlet            | Cadastro **com login automático** após sucesso            |
| `/logoutServlet`                     | GET / POST  | LogoutServlet              | Encerra sessão e limpa cookies "Manter conectado"         |
| `/perfilServlet?aba=colecao\|reviews\|favoritos` | GET | PerfilServlet | Perfil com 3 abas dinâmicas (prints 1, 2 e 3)         |
| `/detalhesDiscoServlet?id=N`         | GET         | DetalhesDiscoServlet       | Página do disco com tracklist, reviews e botões           |
| `/colecaoServlet`                    | POST        | ColecaoServlet             | Adicionar/remover disco da Coleção (`acao=adicionar\|remover`) |
| `/wishlistServlet`                   | POST        | WishlistServlet            | Adicionar/remover disco dos Favoritos                     |
| `/avaliarDiscoServlet`               | POST        | AvaliarDiscoServlet        | Submissão do form de avaliação                            |
| `/buscarServlet?q=...`               | GET         | BuscarServlet              | Resultado da busca do header                              |

A raiz `/` está apontada no `web.xml` para o `listarFeedServlet` (Welcome File), então o Tomcat já abre a Home populada quando o usuário acessa `localhost:8080/`.

---

## 3) Bugs que estavam na branch original e foram corrigidos

1. **Login dava 404** — `LoginServlet` estava em `/login` mas o `login.jsp` enviava para `loginServlet`. Mapeamento corrigido para `@WebServlet("/loginServlet")`.
2. **Cadastro idem** — agora em `@WebServlet("/cadastroServlet")`.
3. **Header com URLs erradas** — `header.jsp` tinha `href="favoritos.jsp"`, `colecao.jsp`, `perfil.jsp`, `logoutServlet` que não existiam. Agora todos apontam para os servlets corretos.
4. **Login redirecionava para `/minhas-colecoes`** (404). Agora vai para `/listarFeedServlet`.
5. **Cadastro não logava o usuário** — agora `AuthService.cadastrarUsuario()` retorna o `Usuario` com o ID gerado, e o `CadastroServlet` joga ele direto na sessão e redireciona para a Home.
6. **Feed da home vinha vazio** — o `index.jsp` antigo tentava acessar `${post.disco.urlCapa}`, mas (a) `Post` nem tinha o objeto `disco`, e (b) o campo no model `Disco` se chama `imagemCapa`, não `urlCapa`. O `index.jsp` foi reescrito para iterar sobre `${discos}` (lista de `Disco` direto), e o `DiscoDAO` ganhou `listarRecentes()`.
7. **AuthFilter bloqueava `/feed`** — antes criava loop com login que ia para `/minhas-colecoes`. Agora a Home é pública (visitantes podem ver o catálogo); só ações que dependem de usuário (`/perfilServlet`, `/colecaoServlet`, `/wishlistServlet`, `/avaliarDiscoServlet`) ficam atrás do filtro.
8. **`UsuarioDAO.criarUsuario()` não retornava o ID** — agora usa `RETURN_GENERATED_KEYS` e popula o `idUsuario` no objeto.
9. **Banco precisava ser inicializado manualmente em `/testar`** — `AppContextListener` agora chama `DatabaseInitializer.init()` no startup do Tomcat automaticamente.

---

## 4) Páginas implementadas a partir dos prints

### Print 4 (Home) → `index.jsp` + `ListarFeedServlet`
Header com busca + ícones, saudação personalizada, 3 seções de cards ("mais vendidos", "mais desejados", "mais colecionados") usando `<c:forEach var="disco" items="${discos}" begin="0" end="5">` para fatiar a mesma lista. Card clicável leva ao `detalhesDiscoServlet`.

### Prints 1/2/3 (Perfil) → `perfil.jsp` + `PerfilServlet`
- Avatar circular com as iniciais do usuário (gerado via JSTL `fn:substring`)
- Bloco com nome, `@username`, "Membro desde YYYY"
- 3 cards de stats (Discos / Reviews / Favoritos) — cada um é um link que muda a aba
- Abas (Coleção / Reviews / Favoritos) controladas pelo parâmetro `?aba=`
- O `PerfilServlet` injeta `${colecao}`, `${reviews}` ou `${favoritos}` conforme a aba
- Pode visualizar perfil de outros usuários: `?username=outroFulano`

### Print do KPop Demon Hunters (Detalhes) → `detalhes.jsp` + `DetalhesDiscoServlet`
- Capa grande à esquerda, info à direita
- Estatística com média + total de avaliações
- Botões "Adicionar à Coleção" e "Adicionar aos Favoritos" como `<form method="POST">` com CSRF natural do servlet
- Estado dos botões muda conforme `${estaNaColecao}` e `${estaNaWishlist}`
- Tracklist (placeholder por enquanto — ver "limitações" abaixo)
- Form de avaliação com 5 estrelas em CSS puro (radio buttons RTL)
- Lista de comentários iterando sobre `${reviews}` (cada item tem `${r.username}`, `${r.nota}`, `${r.comentario}`)

---

## 5) Limitações conhecidas (e o que cada uma exige)

- **Tracklist é mock**: o schema atual não tem tabela `faixa`. O JSP já está pronto para iterar sobre `${faixas}` quando essa tabela existir. Quando você criar a tabela, basta:
  ```sql
  CREATE TABLE faixa (
      id_faixa SERIAL PRIMARY KEY,
      id_disco INT REFERENCES disco(id_disco) ON DELETE CASCADE,
      numero INT, titulo VARCHAR(200), duracao VARCHAR(10)
  );
  ```
  e adicionar `FaixaDAO.listarPorDisco()` retornando uma `List<Faixa>` para o `DetalhesDiscoServlet`.

- **Contador de seguidores na página de perfil é cosmético** (usei "0" placeholder). O schema não tem tabela `seguidor`/`relacao`. Quando criar, basta adicionar `SeguidorDAO.contarSeguidores(idUsuario)` e injetar no `PerfilServlet`.

- **Contador de "likes" em reviews é cosmético** pelo mesmo motivo (não há tabela `curtida_review`).

- **A descrição/bio do usuário está fixa no JSP** ("Colecionador apaixonado por música..."). O schema não tem coluna `bio` em `usuario`. Para fazer dinâmico: `ALTER TABLE usuario ADD COLUMN bio TEXT;` e atualizar o `Usuario` model + `UsuarioDAO.mapear()`.

Nada disso impede o app de funcionar — são incrementos para depois.

---

## 6) Para popular o banco com dados de teste

O `DatabaseInitializer` cria as tabelas mas não popula. Para ver o feed funcionando, rode no PostgreSQL:

```sql
INSERT INTO disco (titulo, artista, ano_lancamento, genero, formato) VALUES
  ('KPop Demon Hunters (Soundtrack From The Netflix Film)', 'Various Artists', 2025, 'K-Pop', 'Vinyl'),
  ('Hidden Treasures', 'Megadeth', 1995, 'Metal', 'Vinyl'),
  ('Elements', 'Skudero', 2024, 'Electronic', 'CD'),
  ('To Record Only Water For Ten Days', 'John Frusciante', 2001, 'Rock', 'Vinyl'),
  ('Collaborations', 'Bruno Mars', 2020, 'Pop', 'CD'),
  ('Beyoncé', 'Beyoncé', 2013, 'R&B', 'CD'),
  ('Yesterday and Today', 'The Beatles', 1966, 'Rock', 'Vinyl'),
  ('The Confessions Tour', 'Madonna', 2007, 'Pop', 'CD'),
  ('Michael', 'Michael Jackson', 2010, 'Pop', 'CD'),
  ('Live From The Los Angeles Sports Arena', 'Pink Floyd', 1980, 'Rock', 'Vinyl'),
  ('Elizabeth Taylor', 'Taylor Swift', 2026, 'Pop', 'Single');
```

---

## 7) Checklist do que pedido no prompt original

- [x] **1. Mapeamento de Rotas (Controllers)**: todos os `@WebServlet` casam com os `action`/`href` dos JSPs.
- [x] **2. Injeção de Dados na Home**: `ListarFeedServlet` busca discos reais via `DiscoDAO.listarRecentes()` e injeta em `${discos}`. `<c:forEach>` em `index.jsp` agora itera corretamente.
- [x] **3. Lógica da Página de Perfil**: 3 abas dinâmicas (`?aba=colecao|reviews|favoritos`) implementadas em `PerfilServlet` + `perfil.jsp`. JSTL itera sobre `${colecao}`, `${reviews}`, `${favoritos}`.
- [x] **4. Lógica de Detalhes do Disco**: `DetalhesDiscoServlet` + `detalhes.jsp` com info, estatística, tracklist, comentários, e botões "Adicionar à Coleção" / "Adicionar a Wishlist" apontando para `colecaoServlet` / `wishlistServlet`.
- [x] **Tudo em `javax.servlet`** (nenhum import `jakarta.*`).
- [x] **Front 100% JSP + JSTL + EL** (zero scriptlets).
- [x] **Erros tratados amigavelmente** (filter + cada servlet com `?erro=...`).
