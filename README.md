# Sistema de Votação para Assembleias

API REST para gerenciar votações em pautas para uma assembleia de associados, desenvolvida com Java e Spring Boot.

Você pode acessar ela aqui: https://rocky-refuge-87494-e6b51cec9d78.herokuapp.com/api

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

A URL base para todos os endpoints desta API é `http://localhost:8080/api`.

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
* **Sucesso Retorna:** `202 Accepted`.
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

## Análise de Performance e Escalabilidade

Para validar o comportamento da aplicação sob alta carga, foi realizado um teste de carga simulando um cenário de votação em
massa. O objetivo foi garantir que a API se mantenha estável, com baixa latência e sem erros, mesmo recebendo centenas de milhares de votos em um curto período.

A ferramenta utilizada para a simulação foi o **Gatling**.

### Cenário de Teste

O teste focou no endpoint mais crítico para a performance de escrita: a submissão de votos.

* **Endpoint Alvo:** `POST /v1/agendas/{agendaId}/votes`
* **Perfil de Carga:** A simulação foi configurada para aumentar gradualmente a carga de usuários e mantê-la por um período, gerando um alto volume de requisições em um curto
  espaço de tempo.
* **Volume Total:** Foram simuladas **100.000** requisições de voto para estressar a capacidade de ingestão da API.

### Otimizações Implementadas

Para garantir a performance sob essa carga, as seguintes decisões arquiteturais e otimizações foram implementadas:

* **Consultas Otimizadas:** Para a validação de cada voto onde precisa-se saber se a sessão existe, foram utilizadas queries JPQL otimizadas.
* **Lazy Loading:** As relações entre VotingSession <-> Agenda e Vote <-> VotingSession foram configuradas com `FetchType.LAZY` como padrão.
* **Validação de Voto Único na Camada de Dados:** Em vez de realizar uma query `SELECT` antes de cada `INSERT` para validar se o associado já votou, essa responsabilidade foi
  delegada ao banco de dados através de uma UNIQUE constraint. Essa abordagem é significativamente mais performática em cenários de alta concorrência.
* **Retorno Controller:** Removido corpo de retorno da requisição, somente status 202 para informar que voto foi computado com sucesso.

### Resultados

O teste de carga foi executado e a aplicação demonstrou alta performance e estabilidade. Abaixo estão as métricas chave extraídas do relatório do Gatling.

| Métrica                         | Valor          | Descrição                                                               |
|:--------------------------------|:---------------|:------------------------------------------------------------------------|
| **Duração do Teste**            | ~3 min         | Tempo total da simulação de carga.                                      |
| **Número Total de Requisições** | 100.000        | Total de votos submetidos com sucesso.                                  |
| **Taxa de Erro (KO)**           | **0 (0%)**     | Nenhuma requisição falhou durante o teste.                              |
| **Throughput (Vazão)**          | **~546 req/s** | Média de requisições por segundo que a API processou.                   |
| **Tempo de Resposta Médio**     | 972ms          | Média geral de tempo de resposta.                                       |
| **Tempo de Resposta (p95)**     | **6643ms**     | 95% de todas as requisições foram respondidas em menos de 6.6 segundos. |
| **Tempo de Resposta (p99)**     | **8748ms**     | 99% de todas as requisições foram respondidas em menos de 8.7 segundos. |

### Conclusão

A análise dos testes de carga valida a robustez e a estabilidade da aplicação. O sistema foi capaz de processar 100.000 requisições com uma taxa de erro de 0%, demonstrando que a arquitetura atual é resiliente e não falha sob estresse.

Os resultados, no entanto, forneceram um insight valioso sobre a latência de escrita sob carga pesada. Os tempos de resposta nos percentis mais altos (p95 e p99) indicam que, embora o sistema não quebre, a experiência do usuário pode ser impactada pela espera da confirmação da escrita síncrona no banco de dados. A investigação confirmou que o gargalo de performance reside na operação de INSERT síncrona na tabela de votos.

### Próximos Passos

Para elevar a performance de escrita a um nível de altíssima concorrência e garantir latências consistentemente baixas (ex: abaixo de 100ms), a evolução natural da arquitetura é a introdução de um padrão de processamento assíncrono ("Fire-and-Forget").

Neste modelo, o fluxo seria:

O endpoint POST .../votes teria uma única responsabilidade: validar o formato da requisição, publicá-la imediatamente em um tópico de mensagens (utilizando ferramentas como Apache Kafka, RabbitMQ ou AWS SQS) e retornar uma resposta 202 Accepted ao cliente.
Um serviço consumidor (worker), rodando em background, seria o responsável por ler os votos da fila em um ritmo otimizado e persisti-los em lote no banco de dados PostgreSQL.
Essa abordagem desacopla completamente a escrita no banco da requisição do usuário, resultando em tempos de resposta de escrita quase instantâneos para o cliente e aumentando drasticamente o throughput (vazão) geral da API.

---