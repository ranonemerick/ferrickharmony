# Ferrick Harmony

**Plataforma de gestão de clínicas construída em arquitetura de microsserviços, com Java 21 e Spring Boot.**

<p>
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway"/>
  <img src="https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" alt="RabbitMQ"/>
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white" alt="Thymeleaf"/>
  <img src="https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger"/>
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
  <img src="https://img.shields.io/badge/Docker%20Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker Compose"/>
  <img src="https://img.shields.io/badge/JUnit5-Testes-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
</p>

---

## Sobre o Projeto

O **Ferrick Harmony** é uma plataforma para o gerenciamento completo de clínicas — pacientes, profissionais de saúde e agendamentos de consultas — organizada como um conjunto de microsserviços independentes que se comunicam de forma assíncrona.

O foco do projeto não é apenas expor endpoints de CRUD, mas garantir a **integridade das regras de negócio** de uma clínica real:

- Impedir que um profissional tenha duas consultas no mesmo horário;
- Validar CPFs com o algoritmo matemático oficial, não apenas com regex superficial;
- Preservar o histórico de pacientes e profissionais através de soft delete, ao invés de exclusão física;
- Proteger todos os recursos com autenticação stateless via JWT;
- Notificar pacientes e profissionais por e-mail sempre que um agendamento é criado ou alterado, sem acoplar essa responsabilidade à API principal.

O repositório é um **monorepo** com dois módulos Maven independentes, cada um com seu próprio banco de dados, ciclo de deploy e `Dockerfile`:

| Módulo                          | Responsabilidade                                                                 | Porta  |
|----------------------------------|-----------------------------------------------------------------------------------|--------|
| `ferrick-harmony-api`            | Core do sistema: pacientes, profissionais, agendamentos, autenticação JWT e i18n | `8080` |
| `ferrick-harmony-notification`   | Consumo de eventos, geração de e-mails HTML e envio via SMTP                     | `8081` |

---

## Arquitetura de Microsserviços & Mensageria

Os dois serviços não se conhecem via HTTP: toda a comunicação entre eles acontece de forma **assíncrona via RabbitMQ**, o que mantém a API principal responsiva mesmo que o envio de e-mails esteja lento ou temporariamente indisponível.

```
┌─────────────────────┐        publish         ┌──────────────────────┐        consume        ┌──────────────────────────┐
│  ferrick-harmony-api │  ───────────────────►  │   RabbitMQ Broker    │  ──────────────────►  │ ferrick-harmony-         │
│  (porta 8080)        │  appointment.exchange  │                      │  appointment.email.   │ notification (porta 8081)│
│                      │  routing-key           │                      │  queue                │                          │
└──────────┬───────────┘                        └──────────────────────┘                        └──────────┬───────────────┘
           │                                                                                                 │
           ▼                                                                                                 ▼
   PostgreSQL (ferrick-harmony)                                                          PostgreSQL (ferrick-harmony-notification)
```

- **`UserProducer`** (na API): publica uma `EmailDTO` no `appointment.exchange` sempre que um agendamento é criado ou atualizado, usando a `appointment.email.routingKey`.
- **`appointment.email.queue`**: fila durável, ligada ao exchange via `Binding`, onde as mensagens ficam até serem processadas.
- **`EmailConsumer`** (na notificação): escuta a fila com `@RabbitListener` e delega o processamento ao `EmailService`.
- O conversor de mensagens é o `Jackson2JsonMessageConverter`, então o payload trafega como JSON, desacoplando os dois serviços de uma implementação de serialização binária.
- Exchange, fila e routing key são configuráveis via `application.yaml` (`broker.exchange.appointment.name`, `broker.queue.email.name`, `broker.routing-key.email.name`) e compartilhados entre os dois módulos.

Cada microsserviço mantém seu **próprio banco de dados** (`ferrick-harmony` e `ferrick-harmony-notification`), seguindo o padrão *database-per-service* — a API nunca acessa diretamente a tabela de e-mails, e vice-versa.

---

## Tecnologias Utilizadas

### `ferrick-harmony-api`

| Categoria       | Tecnologia                                                           |
|------------------|-----------------------------------------------------------------------|
| Linguagem        | Java 21                                                                |
| Framework        | Spring Boot (Web, Data JPA, Validation, Security, AMQP)                |
| Banco de Dados   | PostgreSQL                                                             |
| Migrations       | Flyway (`spring-boot-starter-flyway`)                                 |
| Mensageria       | RabbitMQ (`spring-boot-starter-amqp`) — publica eventos de e-mail     |
| Internacionalização | `MessageSource` (i18n) com resolução via header `Accept-Language`  |
| Documentação     | Springdoc OpenAPI (Swagger UI)                                        |
| Autenticação     | JWT (`com.auth0:java-jwt`) + Spring Security                          |
| Mapeamento       | MapStruct                                                              |
| Produtividade    | Lombok                                                                 |
| Testes           | JUnit 5 + Mockito                                                     |
| Build            | Maven (Maven Wrapper incluído)                                        |

### `ferrick-harmony-notification`

| Categoria       | Tecnologia                                                           |
|------------------|-----------------------------------------------------------------------|
| Linguagem        | Java 21                                                                |
| Framework        | Spring Boot (Web, Data JPA, Validation, Mail, AMQP)                    |
| Banco de Dados   | PostgreSQL (instância isolada, apenas para auditoria de e-mails)      |
| Migrations       | Flyway (`spring-boot-starter-flyway`)                                 |
| Mensageria       | RabbitMQ (`spring-boot-starter-amqp`) — consome eventos via `@RabbitListener` |
| Templates        | Thymeleaf (`spring-boot-starter-thymeleaf`) — e-mails HTML dinâmicos  |
| Envio de E-mail  | `JavaMailSender` via SMTP                                             |
| Produtividade    | Lombok                                                                 |
| Testes           | JUnit 5 + Mockito                                                     |
| Build            | Maven (Maven Wrapper incluído)                                        |

### Infraestrutura compartilhada

| Categoria       | Tecnologia                                                           |
|------------------|-----------------------------------------------------------------------|
| Orquestração     | Docker Compose — sobe API, notificação, RabbitMQ e um banco isolado para cada serviço |
| Broker           | RabbitMQ (imagem `rabbitmq:3-management`, com console de administração) |
| Configuração     | Variáveis de ambiente via arquivo `.env` (não versionado)             |

---

## Funcionalidades de Destaque

### Gestão de Pacientes e Profissionais

CRUD completo para `Patient` e `Professional`, com soft delete: os endpoints de exclusão (`DELETE /patients/deactivate/{id}` e `DELETE /professionals/{id}`) apenas marcam o registro como inativo (`active = false`), preservando o histórico para auditoria e para os agendamentos já existentes.

### Validação Customizada de CPF

A anotação `@ValidCPF` implementa um `ConstraintValidator` próprio que:

- Garante exatamente 11 dígitos numéricos;
- Rejeita sequências repetidas (`00000000000`, `11111111111`, etc.);
- Recalcula os dois dígitos verificadores com o algoritmo oficial do CPF (módulo 11) — não é apenas uma checagem de formato, é uma validação matemática real.

```java
@ValidCPF
private String cpf;
```

### Agendamentos com Prevenção de Conflitos

Ao criar ou atualizar um agendamento, o `AppointmentService`:

1. Verifica se o paciente e o profissional existem e estão ativos;
2. Consulta se o profissional já possui outra consulta não cancelada exatamente na mesma data/hora (`existsByProfessionalIdAndAppointmentDateAndStatusNot`);
3. Lança uma `BusinessException` (traduzida via i18n) caso haja conflito de agenda.

O cancelamento também é lógico: `DELETE /appointments/{id}` apenas move o status para `CANCELED`, liberando o horário para novos agendamentos sem apagar o histórico da consulta.

### Busca Avançada com Specifications

Todos os principais recursos (`patients`, `professionals`, `appointments`) expõem um endpoint `GET /.../parameters` que aceita Filter DTOs combinados dinamicamente via `JpaSpecificationExecutor` — permitindo buscar, por exemplo, agendamentos por nome do paciente, CPF do profissional ou intervalo de datas, sem precisar de uma query fixa para cada combinação de filtros.

Paginação padrão de 20 itens por página (`@PageableDefault(size = 20)`) em todos os endpoints de listagem.

### Mensagens de Erro Internacionalizadas (i18n)

O `GlobalExceptionHandler` centraliza o tratamento de exceções (`EntityNotFoundException`, `MethodArgumentNotValidException`, `BusinessException`) e traduz as mensagens de negócio via `MessageSource`, com bundles em `src/main/resources/i18n/` para inglês (padrão, `messages.properties`) e português (`messages_pt.properties`).

O idioma da resposta é resolvido automaticamente a partir do header HTTP `Accept-Language` da requisição — sem necessidade de nenhum parâmetro extra na URL:

```bash
# Resposta em português
curl http://localhost:8080/patients/999 -H "Accept-Language: pt-BR"

# Resposta em inglês (padrão)
curl http://localhost:8080/patients/999 -H "Accept-Language: en"
```

Caso o header não seja informado ou o idioma solicitado não tenha um bundle correspondente, a aplicação recai automaticamente para o idioma padrão (inglês).

### Motor de Notificações por E-mail (`ferrick-harmony-notification`)

Sempre que um agendamento é criado ou atualizado, a API publica um evento no RabbitMQ (veja [Arquitetura de Microsserviços & Mensageria](#arquitetura-de-microsserviços--mensageria)) em vez de enviar o e-mail diretamente. Isso mantém o fluxo de agendamento rápido e resiliente a falhas ou lentidão no envio de e-mails.

O microsserviço de notificação:

1. Consome a mensagem da fila `appointment.email.queue` através do `EmailConsumer` (`@RabbitListener`);
2. Quando a mensagem traz um template (`appointmentEmail`) e variáveis dinâmicas, processa um arquivo **HTML com Thymeleaf** (`SpringTemplateEngine`) para montar o corpo do e-mail — por exemplo, `appointment-email.html` com dados do paciente, profissional e horário da consulta;
3. Envia o e-mail via SMTP usando `JavaMailSender`/`MimeMessageHelper`, com suporte a corpo HTML e caracteres UTF-8;
4. **Audita cada tentativa de envio no PostgreSQL**, persistindo remetente, destinatário, assunto, corpo processado e o status do disparo (`SENT` ou `ERROR`) — independentemente de o envio ter sucesso ou falhar, garantindo rastreabilidade completa.

A tabela de auditoria de e-mails é criada e versionada por **migrations do Flyway** (`V1__create_table_emails.sql`), no mesmo padrão de versionamento de schema usado pela API principal.

---

## Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:

- **[Java JDK 21](https://adoptium.net/)** ou superior
- **[Maven](https://maven.apache.org/)** (opcional — cada módulo inclui o Maven Wrapper, `mvnw`/`mvnw.cmd`)
- **[Docker](https://www.docker.com/)** e Docker Compose (recomendado — sobe os dois bancos, o RabbitMQ e, opcionalmente, as duas aplicações)
- Uma conta de e-mail com senha de app SMTP (ex.: Gmail) caso queira testar o envio real de notificações

---

## Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/ranonemerick/ferrickharmony.git
cd ferrickharmony
```

### 2. Configure as variáveis de ambiente (`.env`)

O `compose.yaml` na raiz orquestra **toda a infraestrutura**: o banco da API, o banco da notificação, o RabbitMQ e as duas aplicações. Credenciais sensíveis não ficam hardcoded no `compose.yaml` — elas são interpoladas a partir de um arquivo `.env` na raiz do projeto (já protegido pelo `.gitignore`, nunca é versionado).

Crie um arquivo `.env` na raiz com o seguinte conteúdo:

```dotenv
# Banco da API principal
FH_POSTGRES_USER=postgres
FH_POSTGRES_PASSWORD=postgres

# Banco do microsserviço de notificação
FH_NOTIF_POSTGRES_USER=postgres
FH_NOTIF_POSTGRES_PASSWORD=postgres

# Credenciais SMTP usadas pelo ferrick-harmony-notification para enviar e-mails
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-de-app
```

> Se você usa Gmail, `MAIL_PASSWORD` deve ser uma [senha de app](https://myaccount.google.com/apppasswords), não a senha da sua conta.

O segredo usado para assinar os tokens JWT da API é lido separadamente, da variável `JWT_SECRET` (com um valor padrão apenas para desenvolvimento, definido em `application.yaml`):

```bash
# Windows (PowerShell)
$env:JWT_SECRET = "uma-chave-secreta-bem-forte"

# Linux / macOS
export JWT_SECRET="uma-chave-secreta-bem-forte"
```

### 3. Suba a infraestrutura com Docker Compose

O `compose.yaml` sobe cinco serviços: `postgres` (API), `postgres-notification`, `rabbitmq`, `api` e `notification` — cada aplicação com seu próprio banco isolado.

```bash
docker compose up -d
```

Isso builda as imagens da API e da notificação a partir de seus respectivos `Dockerfile`s e já as conecta ao RabbitMQ e aos bancos corretos. Ao final:

- API: `http://localhost:8080`
- Notificação: `http://localhost:8081`
- Console de administração do RabbitMQ: `http://localhost:15672` (usuário/senha padrão: `guest`/`guest`)

Se preferir rodar apenas a infraestrutura (bancos + RabbitMQ) via Docker e as aplicações localmente pela IDE/Maven, suba só esses serviços:

```bash
docker compose up -d postgres postgres-notification rabbitmq
```

### 4. Rode as aplicações localmente (alternativa ao Docker completo)

Com a infraestrutura de apoio no ar (passo anterior), rode cada módulo em um terminal:

```bash
# Terminal 1 — API principal
cd ferrick-harmony-api
./mvnw spring-boot:run      # Linux/macOS
.\mvnw.cmd spring-boot:run  # Windows

# Terminal 2 — Microsserviço de notificação
cd ferrick-harmony-notification
./mvnw spring-boot:run      # Linux/macOS
.\mvnw.cmd spring-boot:run  # Windows
```

As migrations do Flyway rodam automaticamente na subida de cada aplicação: a API cria as tabelas (`users`, `patients`, `professionals`, `appointments`) e semeia um usuário administrador inicial (`admin@admin.com`); a notificação cria a tabela de auditoria de e-mails (`emails`).

---

## Documentação da API (Swagger)

A documentação interativa é gerada automaticamente pelo Springdoc OpenAPI e pode ser acessada em:

```
http://localhost:8080/swagger-ui/index.html
```

O JSON da especificação OpenAPI fica disponível em `http://localhost:8080/v3/api-docs`.

### Autenticação

A API utiliza JWT (Bearer Token) com sessão stateless. Apenas o endpoint de login é público — todos os demais exigem token válido.

**1. Faça login para obter o token:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@admin.com", "password": "sua-senha"}'
```

**2. Use o token retornado no header `Authorization` das próximas requisições:**

```bash
curl http://localhost:8080/patients \
  -H "Authorization: Bearer <seu-token-jwt>"
```

No próprio Swagger UI, clique em **Authorize** e informe `Bearer <seu-token-jwt>` para testar os endpoints protegidos diretamente pela interface.

---

## Como Rodar os Testes

Cada módulo tem sua própria suíte de testes (JUnit 5 + Mockito). Na API, cobrem as regras de negócio da camada de `Service`, incluindo cenários de conflito de agendamento, entidades inativas e validações.

```bash
# API
cd ferrick-harmony-api
./mvnw test      # Linux/macOS
.\mvnw.cmd test  # Windows

# Notificação
cd ferrick-harmony-notification
./mvnw test      # Linux/macOS
.\mvnw.cmd test  # Windows
```

---

## Decisões de Arquitetura

- **DTOs por operação:** cada recurso possui DTOs dedicados (`RequestDTO`, `ResponseDTO`, `UpdateDTO`, `FilterDTO`), evitando expor as entidades JPA diretamente e mantendo os contratos de entrada/saída da API explícitos e estáveis.
- **Mapeamento com MapStruct:** a conversão entre entidades e DTOs é gerada em tempo de compilação, eliminando boilerplate manual e erros de mapeamento.
- **Repository Pattern:** acesso a dados isolado em interfaces `Repository` (Spring Data JPA), estendidas com `JpaSpecificationExecutor` para permitir buscas dinâmicas sem SQL manual.
- **Specification Pattern:** filtros de busca combináveis (`AppointmentSpecification`, `PatientSpecification`, `ProfessionalSpecification`) construídos a partir dos Filter DTOs, mantendo os endpoints de listagem flexíveis sem explodir em métodos de repositório.
- **Anotações Customizadas:** validações de domínio (como `@ValidCPF`) encapsuladas em `ConstraintValidator`, reutilizáveis em qualquer DTO que precise validar um CPF.
- **Global Exception Handler:** um único `@RestControllerAdvice` centraliza o tratamento de erros de validação, recursos não encontrados e regras de negócio violadas, padronizando o formato de resposta de erro (`ApiErrorResponse`) e traduzindo mensagens via `MessageSource` (i18n).
- **Segurança Stateless:** filtro de autenticação (`SecurityFilter`) próprio, interceptando requisições antes do `UsernamePasswordAuthenticationFilter`, sem uso de sessão HTTP — adequado para APIs consumidas por SPAs e aplicativos móveis.
- **Soft Delete:** em vez de `DELETE` físico, os registros de pacientes, profissionais e usuários são apenas inativados (`active = false`), preservando integridade referencial e histórico.
- **Microsserviços desacoplados via mensageria:** a API principal e o serviço de notificação nunca se chamam diretamente por HTTP. A comunicação via RabbitMQ (exchange + fila + routing key) evita que uma falha ou lentidão no envio de e-mails impacte o tempo de resposta da API, e permite escalar cada serviço de forma independente.
- **Database per Service:** cada microsserviço possui seu próprio schema PostgreSQL isolado (`ferrick-harmony` e `ferrick-harmony-notification`), evitando acoplamento no nível de dados entre serviços com ciclos de vida diferentes.
- **Templates de e-mail com Thymeleaf:** o corpo dos e-mails é gerado a partir de templates HTML versionados no próprio código-fonte da notificação, permitindo alterar o layout do e-mail sem tocar em lógica Java, e reaproveitar variáveis dinâmicas (`Context`) por tipo de evento.
- **Auditoria de envio de e-mails:** toda tentativa de envio (sucesso ou falha) é persistida no banco da notificação, com status (`SENT`/`ERROR`), permitindo rastrear entregas e diagnosticar problemas de SMTP sem depender apenas de logs.
- **Configuração via `.env`:** segredos e credenciais (senhas de banco, credenciais SMTP) nunca ficam hardcoded no `compose.yaml` nem são versionados — são lidos de um arquivo `.env` local, listado no `.gitignore`.

---

## Licença

Este projeto ainda não possui uma licença definida. Sinta-se à vontade para abrir uma issue caso queira propor uma.

---

<p align="center">Desenvolvido com Spring Boot por <strong>Ranon Campos</strong></p>
