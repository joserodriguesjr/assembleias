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

## Pré-requisitos

* Java (JDK) 21 ou superior
* Docker e Docker Compose (para rodar o PostgreSQL localmente)

## Como Executar a Aplicação

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/joserodriguesjr/assembleias.git](https://github.com/joserodriguesjr/assembleias.git)
   cd assembleias
   ```

2. **Dê permissão de execução ao Gradle Wrapper** (necessário apenas na primeira vez em ambientes Linux/macOS):

   ```bash
   chmod +x ./gradlew
   ```

3. **Inicie o banco de dados com Docker Compose:**

   ```bash
   docker-compose up -d
   ```

4. **Execute a aplicação com o Gradle Wrapper:**

    * **Linux/macOS:**
      ```bash
      ./gradlew bootRun
      ```
    * **Windows:**
      ```bash
      gradlew.bat bootRun
      ```

A aplicação estará disponível em `http://localhost:8080`.

## Como Construir o Projeto (Gerar o .jar)

Para gerar o arquivo `.jar` executável da aplicação, use o comando `build`:

* **Linux/macOS:**
  ```bash
  ./gradlew build
  ```
* **Windows:**
  ```bash
  gradlew.bat build
  ```

O arquivo `.jar` final será criado em `build/libs/`.


---