# 🥘 SGPB API — Sistema de Gestão de Paella Beneficente (ABADEUS)

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-Frontend_Embutido-DD0031?style=for-the-badge&logo=angular&logoColor=white)

Backend REST desenvolvido em **Java com Spring Boot** para o **Sistema de Gestão de Paella Beneficente (SGPB)**, criado para apoiar a **12ª Edição Solidária da Paella Beneficente ABADEUS** (Criciúma/SC — 28/11/2026).

Além de expor toda a API consumida pelo aplicativo mobile (`sgpb-app`), este repositório também embute o **site institucional em Angular**, servido diretamente pela pasta de recursos estáticos do Spring Boot (`src/main/resources/static`).

---

## 📋 Índice

- [Arquitetura e Stack](#-arquitetura-e-stack)
- [Regras de Negócio Implementadas](#-regras-de-negócio-implementadas)
- [Endpoints da API](#-endpoints-da-api)
- [Site Institucional Integrado](#-site-institucional-integrado)
- [Como Executar](#-como-executar)
- [Usuários de Teste (DataSeeder)](#-usuários-de-teste-dataseeder)
- [Desenvolvimento e Build do Frontend Angular](#-desenvolvimento-e-build-do-frontend-angular)

---

## 🧱 Arquitetura e Stack

| Camada | Tecnologia |
| :--- | :--- |
| **Backend** | Java 17+, Spring Boot, Spring Web, Spring Data JPA / Hibernate |
| **Frontend Web (Embutido)** | Angular (build de produção servido via `src/main/resources/static`) |
| **Integração Mobile** | API REST JSON consumida pelo app React Native / Expo (`sgpb-app`) |
| **Carga Inicial** | `DataSeeder.java` (evento, cardápio e perfis de acesso) |

---

## ⚙️ Regras de Negócio Implementadas

- **Reserva de Estoque com Expiração (15 min):** ao criar um pedido (`PENDING`), o estoque dos itens é reservado temporariamente. Caso o pagamento não seja confirmado dentro de 15 minutos, o pedido expira e os itens retornam automaticamente ao estoque disponível.
- **Pagamento Idempotente:** a confirmação de pagamento (`POST /api/orders/{id}/pay`) garante consistência mesmo em caso de requisições duplicadas, alterando o status para `PAID`.
- **Doação Pedagógica Adicional:** suporte ao campo `donationAmount` em `Order`, registrando contribuições extras separadamente do valor dos itens do cardápio.
- **Controle de Concorrência e Dupla Retirada:** a validação de retirada (`/api/pickups/validate`) impede que um mesmo ingresso/pedido seja entregue mais de uma vez.
- **Contingência Offline:** endpoints dedicados (`/api/pickups/sync-data` e `/api/pickups/sync-offline`) permitem que o aplicativo mobile opere com cache local em SQLite no dia do evento e sincronize as retiradas assim que a conexão retornar.
- **Trilha de Auditoria:** todas as mudanças de status de um pedido são registradas em `OrderStatusHistory` e consultáveis via API.
- **Cancelamento e Reembolso Auditados:** operações de reembolso exigem justificativa obrigatória registrada no histórico.
- **Segurança de Autenticação:** trava automática de segurança após **5 tentativas inválidas consecutivas de login**.
- **Controle de Cardápio e Lotes:** gerenciamento de produtos ativos/inativos (`isActive`), edição de preços, estoque e criação de lotes.

---

## 🔗 Endpoints da API

Base URL local: `http://localhost:8080/api`

### Autenticação e Equipe (`/api/auth`)
| Método | Rota | Descrição |
| :---: | :--- | :--- |
| `POST` | `/api/auth/login` | Autentica usuário (com bloqueio após 5 falhas) |
| `POST` | `/api/auth/register` | Cadastra novo apoiador (comprador) |
| `GET` / `POST` | `/api/auth/staff` | Lista ou cadastra membros da equipe (Admin, Voluntário, Parceiro) |

### Eventos, Cardápio e Lotes (`/api/events`)
| Método | Rota | Descrição |
| :---: | :--- | :--- |
| `GET` | `/api/events/{eventId}/products` | Lista os produtos do cardápio do evento |
| `PUT` | `/api/events/{eventId}` | Atualiza informações do evento |
| `PUT` | `/api/events/products/{productId}` | Atualiza dados, preço e estoque de um produto |
| `PATCH` | `/api/events/products/{productId}/toggle` | Ativa ou desativa um produto (`isActive`) |
| `POST` | `/api/events/{eventId}/batches` | Cria um novo lote de vendas |

### Pedidos e Dashboard (`/api/orders`)
| Método | Rota | Descrição |
| :---: | :--- | :--- |
| `POST` | `/api/orders` | Cria pedido (`PENDING`) com reserva de estoque e `donationAmount` |
| `GET` | `/api/orders` | Lista pedidos para gestão administrativa |
| `GET` | `/api/orders/by-number/{orderNumber}` | Busca resumo do pedido pelo número (conferência pré-retirada) |
| `GET` | `/api/orders/{orderId}/history` | Retorna trilha de auditoria (`OrderStatusHistory`) |
| `POST` | `/api/orders/{id}/pay` | Confirma pagamento de forma idempotente (`PAID`) |
| `POST` | `/api/orders/{id}/cancel` | Cancela pedido e estorna reserva de estoque |
| `POST` | `/api/orders/{id}/refund` | Realiza reembolso mediante justificativa obrigatória |
| `GET` | `/api/orders/dashboard` | Retorna indicadores financeiros e operacionais do evento |

### Check-in e Sincronização Offline (`/api/pickups`)
| Método | Rota | Descrição |
| :---: | :--- | :--- |
| `POST` | `/api/pickups/validate` | Valida e confirma a retirada do pedido (bloqueia dupla entrega) |
| `GET` | `/api/pickups/sync-data` | Exporta pedidos pagos para cache offline (SQLite do app) |
| `POST` | `/api/pickups/sync-offline` | Recebe e sincroniza retiradas realizadas offline |

---

## 🌐 Site Institucional Integrado

O site institucional em **Angular** é servido diretamente na raiz da aplicação (`http://localhost:8080`) e contém:
- Informações atualizadas da **12ª Edição Solidária** (28/11/2026 — Criciúma/SC).
- Cardápio sincronizado com o backend.
- Botão **BAIXE O APP!** no cabeçalho, que abre um modal com **QR Code dinâmico** e link direto para o build Android (Expo EAS) do aplicativo Paelito.

---

## 🚀 Como Executar

### Pré-requisitos
- **Java JDK 17** ou superior

### Subir a aplicação
```bash
git clone <URL_DO_REPOSITORIO>
cd sgpb-api
./mvnw spring-boot:run
```

> No Windows, utilize `mvnw.cmd spring-boot:run`.

Após a inicialização:
- **Site institucional:** `http://localhost:8080`
- **API REST:** `http://localhost:8080/api`

---

## 🔐 Usuários de Teste (DataSeeder)

Os usuários abaixo são criados automaticamente na inicialização da aplicação:

| Perfil | E-mail | Senha | Escopo |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@abadeus.com.br` | `ADM123` | Dashboard, Pedidos, Reembolso, CSV, Estoque, Equipe e Check-in |
| **Voluntário** | `voluntario@abadeus.com.br` | `VOL123` | Check-in de retirada e conferência de pedidos |
| **Parceiro** | `parceiro@abadeus.com.br` | `PAR123` | Registro de vendas em ponto parceiro |

---

## 🛠️ Desenvolvimento e Build do Frontend Angular

O build de produção já está versionado em `src/main/resources/static/`, não sendo necessário rodar o Angular separadamente para avaliar o projeto.

Caso deseje alterar o site institucional em desenvolvimento:

```bash
npm install
npm start
```

Para recompilar o frontend e atualizar os arquivos estáticos servidos pelo Spring Boot:

```bash
rm -rf dist/ && npm run build && \
rm -rf src/main/resources/static/* target/classes/static/* && \
(cp -r dist/sgpb-frontend/browser/* src/main/resources/static/ 2>/dev/null || cp -r dist/sgpb-frontend/* src/main/resources/static/) && \
cp -r src/main/resources/static/* target/classes/static/
```

**Projeto Paella Beneficente | ABADEUS**

Desenvolvido com dedicação para apoiar uma boa causa.

</div>
