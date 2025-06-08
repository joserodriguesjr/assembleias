# Sistema de Votação para Assembleias

API REST para gerenciar votações em pautas para uma assembleia de associados, desenvolvida com Java e Spring Boot.

## Funcionalidades

* Cadastro de novas pautas.
* Abertura de sessões de votação com tempo definido.
* Recebimento de votos (Sim/Não).
* Apuração automática dos resultados após o término da sessão.

## Arquitetura e Tecnologias

Este projeto segue os princípios da **Clean Architecture**, dividindo o código nas camadas `domain`, `application` e `infrastructure`.

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.5.0
* **Build Tool:** Gradle
* **Banco de Dados:** PostgreSQL
* **Persistência:** Spring Data JPA / Hibernate
* **Testes:** JUnit 5, Mockito
* **Logging:** SLF4J
* **Documentação da API:** Springdoc (OpenAPI 3)

## Como Executar a Aplicação

### Pré-requisitos

* Docker e Docker Compose (para rodar o PostgreSQL localmente)

### Etapas

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/joserodriguesjr/assembleias.git
   cd assembleias
   ```

2. **Inicie a aplicação com Docker Compose:**

   ```bash
   docker-compose up
   ```

A aplicação estará disponível em `http://localhost:8080/api`.

## Documentação da API (Swagger UI)

Após iniciar a aplicação, a documentação interativa da API estará disponível no seguinte endereço:

[http://localhost:8080/api/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Documentação da API (Endpoints)

A URL base para todos os endpoints desta API é `http://localhost:8080`.

### Resumo dos Endpoints

| Método HTTP | Endpoint                         | Descrição                                        |
|:------------|:---------------------------------|:-------------------------------------------------|
| `POST`      | `/v1/agendas`                    | Cria uma nova pauta.                             |
| `GET`       | `/v1/agendas/{agendaId}`         | Busca os detalhes de uma pauta e sua sessão.     |
| `POST`      | `/v1/agendas/{agendaId}/session` | Abre uma nova sessão de votação para uma pauta.  |
| `POST`      | `/v1/agendas/{agendaId}/votes`   | Submete um voto em uma sessão de votação aberta. |
| `GET`       | `/v1/agendas/{agendaId}/results` | Obtém os resultados da votação para uma pauta.   |

---

### Detalhes dos Endpoints

#### 1. Criar uma Nova Pauta

Registra uma nova pauta no sistema.

* **Endpoint:** `POST /v1/agendas`
* **Sucesso Retorna:** `201 Created` com o corpo da pauta criada.
* **Exemplo de `curl`:**

    ```bash
    curl -X POST http://localhost:8080/v1/agendas \
    -H "Content-Type: application/json" \
    -d '{"name": "Devemos definir novo orçamento para 2026?"}'
    ```

* **Corpo da Requisição (`CreateAgenda`):**

    ```json
    {
      "name": "string"
    }
    ```

---

#### 2. Buscar uma Pauta por ID

Retorna os detalhes de uma pauta específica, incluindo informações sobre sua sessão de votação, se houver.

* **Endpoint:** `GET /v1/agendas/{agendaId}`
* **Sucesso Retorna:** `200 OK` com os detalhes da pauta.
* **Exemplo de `curl`:**

    ```bash
    curl -X GET http://localhost:8080/v1/agendas/1
    ```

* **Corpo da Resposta (`AgendaDetailsDTO`):**

    * **Se houver uma sessão:**

        ```json
        {
          "agendaId": 1,
          "title": "Devemos definir novo orçamento para 2026?",
          "session": {
            "sessionId": 1,
            "agenda" : { },
            "startTime": "2025-06-08T20:10:00.000000",
            "endTime": "2025-06-08T20:11:00.000000",
            "yesVotes": 0,
            "noVotes": 0,
            "processed": false
          }
        }
        ```

    * **Se não houver sessão:**

        ```json
        {
          "agendaId": 1,
          "title": "Devemos definir novo orçamento para 2026?",
          "session": null
        }
        ```

---

#### 3. Abrir uma Sessão de Votação

Abre uma sessão de votação para uma pauta. A duração padrão é de 1 minuto, mas pode ser customizada enviando um corpo na requisição.

* **Endpoint:** `POST /v1/agendas/{agendaId}/session`
* **Sucesso Retorna:** `201 Created` com o corpo da sessão criada.

* **Cenário A: Duração Padrão (1 minuto)**

    * **Exemplo de `curl`:**

        ```bash
        curl -X POST http://localhost:8080/v1/agendas/1/session
        ```

    * **Corpo da Requisição:** Vazio.

* **Cenário B: Duração Customizada**

    * **Exemplo de `curl`:**

        ```bash
        curl -X POST http://localhost:8080/v1/agendas/1/session \
        -H "Content-Type: application/json" \
        -d '{"endTime": "2025-06-09T10:00:00"}'
        ```

    * **Corpo da Requisição (`CreateVotingSession`):**

        ```json
        {
          "endTime": "2025-06-09T10:00:00"
        }
        ```

---

#### 4. Submeter um Voto

Registra o voto de um associado (SIM/NAO) em uma sessão de votação que esteja aberta.

* **Endpoint:** `POST /v1/agendas/{agendaId}/votes`
* **Sucesso Retorna:** `202 Accepted` com o corpo do voto registrado.
* **Exemplo de `curl`:**

    ```bash
    curl -X POST http://localhost:8080/v1/agendas/1/votes \
    -H "Content-Type: application/json" \
    -d '{"associateId": "123.456.789-00", "choice": "SIM"}'
    ```

* **Corpo da Requisição (`CreateVoteRequest`):**

    ```json
    {
      "associateId": "string",
      "choice": "SIM"
    }
    ```

  *O campo `choice` aceita os valores `SIM` ou `NAO`.*

---

#### 5. Obter Resultados da Votação

Busca o estado atual da sessão de votação. Se a sessão já terminou e foi processada pelo sistema, os campos `yesVotes` e `noVotes` estarão preenchidos com o resultado final.

* **Endpoint:** `GET /v1/agendas/{agendaId}/results`
* **Sucesso Retorna:** `202 Accepted` com o corpo da sessão de votação.
* **Exemplo de `curl`:**

    ```bash
    curl -X GET http://localhost:8080/v1/agendas/1/results
    ```

* **Corpo da Resposta (`VotingSession`):**
  A resposta será o objeto da sessão. Se o `processed` for `true`, a contagem de votos estará disponível.

    ```json
    {
      "id": 1,
      "startTime": "2025-06-08T20:10:00.000000",
      "endTime": "2025-06-08T20:11:00.000000",
      "yesVotes": 150,
      "noVotes": 75,
      "processed": true
    }
    ```

---