cat << 'EOF' > README.md
# 🥘 SGPB API — Sistema de Gestão da Paella Beneficente (ABADEUS)

API RESTful desenvolvida em **Java 25** e **Spring Boot** para o gerenciamento centralizado de vendas, controle de estoque em tempo real, pagamentos via Pix e validação de retirada de pedidos da **Paella Beneficente ABADEUS**.

---

## Tecnologias utilizadas

* **Java 25**
* **Spring Boot** (Spring Web, Spring Data JPA, Spring Scheduling)
* **PostgreSQL 16** (Containerizado via Docker Compose)
* **Hibernate** (ORM e geração automática das 12 tabelas de domínio e auditoria)
* **Lombok** (Redução de boilerplate)
* **Maven** (Gerenciamento de dependências e build)

---

## Regras de negócio & arquitetura

1. **Reserva Temporária de Estoque (TTL de 15 minutos):** Ao criar um pedido (`PENDING`), o estoque do produto é reservado imediatamente e um código **Pix Copia e Cola (BRCode)** é gerado com validade de 15 minutos (`expiresAt`).
2. **Expiração Automática (`@Scheduled`):** Um *worker* em segundo plano verifica a cada 60 segundos os pedidos pendentes vencidos, alterando o status para `EXPIRED` e devolvendo automaticamente as porções ao estoque disponível.
3. **Confirmação de Pagamento Idempotente:** O endpoint de confirmação valida se o pedido já foi pago ou expirou, gerando o registro financeiro, o histórico de status e liberando o QR Code único de retirada.
4. **Trava contra Retirada Duplicada (Anti-Fraude):** Na leitura do QR Code no dia do evento, o sistema valida se o pedido está `PAID` e bloqueia imediatamente qualquer tentativa de segunda retirada (`REDEEMED`), registrando o log de auditoria e o voluntário responsável.
5. **Data Seeder Automático:** Ao subir a aplicação com o banco limpo, o `DataSeeder` popula automaticamente o Evento inicial, o 1º Lote, os 3 Produtos oficiais (*Paella Individual*, *Paella Familiar* e *Combo Especial* com fotos) e a credencial de Administrador.

---

## Executar o projeto localmente

### Pré-requisitos
* **Docker** e **Docker Compose** instalados
* **JDK 25** (ou utilização do Maven Wrapper `./mvnw` incluso no projeto)

### Subir o banco de dados (PostgreSQL)
Na raiz do projeto (`sgpb-api`), execute:

```bash
docker compose up -d
<div align="center">

# 🍽️ SGPB — Sistema de Gestão de Projeto Beneficente

**Uma plataforma para organizar, divulgar e apoiar ações beneficentes.**

Desenvolvido para o projeto ABADEUS.

</div>

---

## 📌 Sobre o projeto

O **SGPB (Sistema de Gestão de Projeto Beneficente)** é uma aplicação desenvolvida para apoiar a organização e divulgação de eventos beneficentes, facilitando o acesso às informações do projeto, a visualização do cardápio e o acompanhamento das ações de arrecadação.

O sistema busca oferecer uma experiência simples, intuitiva e acessível, conectando a comunidade às iniciativas promovidas pela ABADEUS.

## ✨ Funcionalidades

- **Página inicial:** apresentação do projeto e informações principais.
- **Evento:** divulgação das informações relacionadas ao evento beneficente.
- **ABADEUS:** apresentação da instituição e de sua iniciativa.
- **Cardápio:** exibição dos produtos disponíveis, com descrições, ingredientes e valores.
- **Arrecadação:** apresentação da meta e do progresso da campanha.
- **FAQ:** seção de perguntas frequentes para orientar os visitantes.
- **Contato:** informações e canais para comunicação.

## 🛠️ Tecnologias utilizadas

| Tecnologia | Aplicação |
|---|---|
| Angular 21 | Desenvolvimento da aplicação frontend |
| TypeScript | Lógica e tipagem da aplicação |
| HTML5 | Estrutura das páginas |
| SCSS | Estilização e responsividade |
| Node.js / npm | Ambiente e gerenciamento de dependências |

## 📁 Estrutura do projeto

```text
SgpbFrontend/
│
├── public/
│   └── images/
│
├── src/
│   ├── app/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   └── app.component.ts
│   │
│   ├── index.html
│   ├── main.ts
│   └── styles.scss
│
├── angular.json
├── package.json
├── tsconfig.json
└── README.md
```

## ⚙️ Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- [Node.js](https://nodejs.org/)
- npm (instalado junto com o Node.js)
- [Angular CLI](https://angular.dev/tools/cli)

Para verificar as versões instaladas:

```bash
node -v
npm -v
ng version
```

## 🚀 Como executar o projeto

**1. Clone o repositório**

```bash
git clone URL_DO_REPOSITORIO
```

**2. Acesse a pasta do projeto**

```bash
cd SgpbFrontend
```

**3. Instale as dependências**

```bash
npm install
```

**4. Execute a aplicação**

```bash
ng serve
```

**5. Acesse no navegador**

```text
http://localhost:4200/
```

A aplicação será atualizada automaticamente durante o desenvolvimento sempre que alterações forem realizadas nos arquivos.

## 🏗️ Build

Para gerar a versão de produção:

```bash
ng build
```

Os arquivos compilados serão disponibilizados no diretório `dist/`.

## 🧪 Testes

Para executar os testes do projeto:

```bash
ng test
```

## 🎨 Identidade visual

A interface utiliza uma identidade visual inspirada na proposta do projeto beneficente, com elementos acolhedores, cores quentes e componentes visuais que reforçam a comunicação da campanha.

## 🤝 Projeto

Desenvolvido como parte do **Projeto Paella Beneficente — ABADEUS**, com o objetivo de contribuir para a divulgação e organização da iniciativa.

---

<div align="center">

**Projeto Paella Beneficente | ABADEUS**

Desenvolvido com dedicação para apoiar uma boa causa.

</div>
