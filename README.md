# Repositório para o trabalho de introdução ao desenvolvimento web.

AAA
# 🧩 Enunciado do Projeto: Construção de Sistema Web Seguro com Arquitetura MVC

## 🎯 Objetivo do Projeto
Desenvolver um sistema Web completo e responsivo, aplicando os conceitos fundamentais de desenvolvimento **Front-end** e **Back-end**.

O sistema deve ser projetado sob a arquitetura **MVC (Model-View-Controller)**, garantindo:
- Separação clara entre lógica de negócios
- Controle de requisições
- Interface com o usuário

Além disso, o projeto focará em práticas reais de mercado, incluindo:
- Controle de versão
- Automação
- Persistência de dados em múltiplos níveis
- Segurança da aplicação

---

## 🛠️ Tecnologias Exigidas

### 🎨 Front-end
- **HTML5** → Estruturação semântica  
- **CSS3** → Estilização e layout responsivo  
- **JavaScript** → Interatividade dinâmica e manipulação do DOM  

### ⚙️ Back-end
- **Java**
- **Servlets**
- **JSP (JavaServer Pages)**

---

## 🏗️ Arquitetura e Padrões (MVC)

O sistema deve implementar rigorosamente o padrão **MVC**:

### 🔹 Controller (Controlador)
- Implementado com **Servlets**
- Responsável por:
  - Interceptar requisições HTTP
  - Processar dados de entrada
  - Interagir com o Model
  - Encaminhar respostas usando `RequestDispatcher.forward()`

### 🔹 Model (Modelo)
- Implementado com:
  - **JavaBeans**
  - Classes Java comuns
- Responsável por:
  - Regra de negócio
  - Acesso aos dados

### 🔹 View (Visão)
- Implementada com **JSP**
- Responsável por:
  - Renderizar HTML
  - Exibir dados enviados pelo Controller
- ❗ Não deve conter lógica complexa

---

## 💾 Armazenamento de Dados

O sistema deve utilizar múltiplos níveis de persistência:

### 🗄️ Banco de Dados Relacional
- Persistência definitiva dos dados
- Recomendado:
  - **Connection Pool**
  - Evitar abrir conexão a cada requisição

### 🔐 Sessões (Session)
- Uso de `HttpSession`
- Armazenamento temporário:
  - Usuário logado
  - Carrinho de compras

### 🍪 Cookies
- Armazenamento no navegador
- Persistência mesmo após fechar o browser
- Uso para preferências do usuário

### ⚡ Cache de Aplicação
- Uso de headers HTTP:
  - `Cache-Control`
  - `Expires`
- Objetivo:
  - Melhorar performance
  - Evitar cache de páginas sensíveis

---

## 🔒 Segurança (Security Support)

### 🧪 Front-end
- Validação de formulários com **JavaScript**
- Prevenção de dados inválidos e scripts maliciosos

### 🛡️ Back-end
Implementar:
- **Autenticação**
- **Autorização**

Opções:
- Form-Based Authentication
- Basic Authentication (HTTP)

Requisitos:
- Validação de credenciais
- Associação de usuários a perfis (**roles**)
- Proteção de páginas JSP
- Verificação de sessão ativa nos Servlets

---

## 🔄 Controle de Versão (GitHub)

O projeto deve obrigatoriamente utilizar **GitHub**, seguindo boas práticas:

- Criação de **branches**
- Commits com mensagens claras
- Uso de **Pull Requests**
- Resolução de conflitos

---

## 📦 Entregas e Avaliação

### 📁 Entregas
- Repositório no GitHub contendo:
  - Código-fonte completo
  - Scripts do banco de dados
  - Arquivo `README.md` com:
    - Explicação da arquitetura
    - Instruções para compilação e execução

### 🎤 Apresentação
- Demonstração do sistema funcionando
- Avaliação de:
  - Uso correto do MVC
  - Segurança implementada
  - Uso das tecnologias exigidas

### 📊 Avaliação
- **Individual**:
  - Baseada nos commits e Pull Requests

- **Coletiva**:
  - Baseada no funcionamento geral do sistema
