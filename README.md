# Projeto de Sistema de Votação para Assembleias

API REST para gerenciar votações em pautas para uma assembleia de associados, desenvolvida com Java e Spring Boot.

## Funcionalidades

* Cadastro de novas pautas.
* Abertura de sessões de votação com tempo definido.
* Recebimento de votos (Sim/Não).
* Apuração automática dos resultados após o término da sessão.
  todo: Não é automatica

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

## Pré-requisitos

* Java (JDK) 21 ou superior
* Docker e Docker Compose (para rodar o PostgreSQL localmente)

## Como Executar a Aplicação

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

[http://localhost:8080/swagger-ui.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui.html)

---