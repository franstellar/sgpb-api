cat << 'EOF' > README.md
# 🥘 SGPB API — Sistema de Gestão da Paella Beneficente (ABADEUS)

API RESTful desenvolvida em **Java 21** e **Spring Boot** para o gerenciamento centralizado de vendas, controle de estoque em tempo real, pagamentos via Pix e validação de retirada de pedidos da **Paella Beneficente ABADEUS**.

---

## Tecnologias utilizadas

* **Java 21**
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
* **JDK 21** (ou utilização do Maven Wrapper `./mvnw` incluso no projeto)

### Subir o banco de dados (PostgreSQL)
Na raiz do projeto (`sgpb-api`), execute:

```bash
docker compose up -d
