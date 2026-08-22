# Ferrick Harmony API

**API RESTful para gestão de clínicas, construída com Spring Boot.**

<p>
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway"/>
  <img src="https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger"/>
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
  <img src="https://img.shields.io/badge/JUnit5-Testes-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
</p>

---

## Sobre o Projeto

O **Ferrick Harmony API** é uma API RESTful construída com **Spring Boot** para o gerenciamento completo de clínicas — pacientes, profissionais de saúde e agendamentos de consultas.

O foco do projeto não é apenas expor endpoints de CRUD, mas garantir a **integridade das regras de negócio** de uma clínica real:

- Impedir que um profissional tenha duas consultas no mesmo horário;
- Validar CPFs com o algoritmo matemático oficial, não apenas com regex superficial;
- Preservar o histórico de pacientes e profissionais através de soft delete, ao invés de exclusão física;
- Proteger todos os recursos com autenticação stateless via JWT.

O objetivo é servir como o backend de um sistema de gestão de clínicas (agendas, prontuário básico e cadastro de equipe), pronto para ser consumido por um front-end web ou mobile.

---

## Tecnologias Utilizadas

| Categoria       | Tecnologia                                                           |
|------------------|-----------------------------------------------------------------------|
| Linguagem        | Java 21                                                                |
| Framework        | Spring Boot (Web, Data JPA, Validation, Security)                     |
| Banco de Dados   | PostgreSQL                                                             |
| Migrations       | Flyway (`spring-boot-starter-flyway`)                                 |
| Documentação     | Springdoc OpenAPI (Swagger UI)                                        |
| Autenticação     | JWT (`com.auth0:java-jwt`) + Spring Security                          |
| Mapeamento       | MapStruct                                                              |
| Produtividade    | Lombok                                                                 |
| Testes           | JUnit 5 + Mockito                                                     |
| Containers       | Docker Compose (integração nativa via `spring-boot-docker-compose`)   |
| Build            | Maven (Maven Wrapper incluído)                                        |

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

### Mensagens de Erro Internacionalizadas

O `GlobalExceptionHandler` centraliza o tratamento de exceções (`EntityNotFoundException`, `MethodArgumentNotValidException`, `BusinessException`) e traduz as mensagens de negócio via `MessageSource`, com suporte a `pt` e fallback padrão.

---

## Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:

- **[Java JDK 21](https://adoptium.net/)** ou superior
- **[Maven](https://maven.apache.org/)** (opcional — o projeto inclui o Maven Wrapper, `mvnw`/`mvnw.cmd`)
- **[Docker](https://www.docker.com/)** e Docker Compose (recomendado — sobe o PostgreSQL automaticamente)
- Alternativamente, uma instância local do PostgreSQL já em execução

---

## Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/ranonemerick/ferrickharmony.git
cd ferrickharmony
```

### 2. Suba o banco de dados

O projeto já traz um `compose.yaml` na raiz com um serviço PostgreSQL pronto para uso:

```yaml
services:
  postgres:
    image: 'postgres:latest'
    environment:
      - 'POSTGRES_DB=ferrick-harmony'
      - 'POSTGRES_PASSWORD=${FH_POSTGRES_PASSWORD:-postgres}'
      - 'POSTGRES_USER=${FH_POSTGRES_USER:-postgres}'
    ports:
      - '5433:5432'
```

Graças à dependência `spring-boot-docker-compose`, não é necessário subir o container manualmente. Ao rodar a aplicação com o Docker ativo, o Spring Boot detecta o `compose.yaml`, sobe o container do PostgreSQL automaticamente e configura o `DataSource` sozinho. Se preferir subir manualmente, use:

```bash
docker compose up -d
```

### 3. Configure as variáveis de ambiente

O segredo usado para assinar os tokens JWT é lido da variável `JWT_SECRET` (com um valor padrão apenas para desenvolvimento):

```bash
# Windows (PowerShell)
$env:JWT_SECRET = "uma-chave-secreta-bem-forte"

# Linux / macOS
export JWT_SECRET="uma-chave-secreta-bem-forte"
```

Se você optar por não usar o Docker Compose automático, configure também a conexão com o banco em `application.yaml` (ou via variáveis de ambiente `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).

### 4. Rode a aplicação

```bash
cd ferrick-harmony-api

# Linux / macOS
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

As migrations do Flyway rodam automaticamente na subida da aplicação, criando as tabelas (`users`, `patients`, `professionals`, `appointments`) e semeando um usuário administrador inicial (`admin@admin.com`) para o primeiro acesso.

A API estará disponível em:

```
http://localhost:8080
```

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

O projeto conta com testes unitários (JUnit 5 + Mockito) cobrindo as regras de negócio da camada de `Service`, incluindo cenários de conflito de agendamento, entidades inativas e validações.

```bash
cd ferrick-harmony-api

# Linux / macOS
./mvnw test

# Windows
.\mvnw.cmd test
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

---

## Licença

Este projeto ainda não possui uma licença definida. Sinta-se à vontade para abrir uma issue caso queira propor uma.

---

<p align="center">Desenvolvido com Spring Boot por <strong>Ranon Campos</strong></p>
