# Spring Boot 3 API

Este projeto é uma API desenvolvida em Spring Boot 3.3.3. Este README fornece instruções para desenvolvedores que desejam rodar o projeto localmente, incluindo como rodar o JAR usando Maven, configurar o ambiente no IntelliJ IDEA, utilizar arquivos `.env` para gerenciar variáveis de ambiente e como rodar o PostgreSQL e o SonarQube usando Docker Compose.

## Pré-requisitos

- Java 21+
- Maven 3.6+
- Docker e Docker Compose
- IntelliJ IDEA

## Rodando o JAR usando Maven

1. Clone o repositório:
    ```bash
    git clone https://github.com/nexus-project-v/nexus-change-api
    cd nexus-change-api
    ```

2. Compile e rode o JAR:
    ```bash
    mvn clean install
    java -jar target/nexus-change-api.jar
    ```

## Configurando o Ambiente no IntelliJ IDEA

1. Abra o IntelliJ IDEA e selecione `File -> Open...` e escolha o diretório do projeto.

   2. Configure as variáveis de ambiente:
       - Clique com o botão direito no projeto no painel lateral e selecione `Edit Configurations...`.
       - Clique no ícone `+` no canto superior esquerdo e selecione `Application`.
         - Configure os campos:
             - **Name**: Nome do projeto
             - **Main class**: `br.com.sevenfood.client.sevenfoodclientapi.RunApplication` (substitua `br.com.sevenfood.client.sevenfoodclientapi.RunApplication` pela sua classe principal)
             - **VM options**: `-Dspring.profiles.active=prod` (Configuração para perfil de produção)
             - **Environment variables**: Clique no ícone `...` e adicione as variáveis necessárias:
               ```properties
                DATABASE_PASSWORD=<<PASSWORD>>!;
                DATABASE_URL=jdbc:postgresql://IP:PORTA/DATABASE_NAME;
                DATABASE_USERNAME=DATABASE_USERNAME;
                KAFKA_BOOTSTRAP_SERVERS=IP:9092;
                OTLP_URL=http://IP:4317;
                PAYMENT_PROVIDER=provider-local;
                SECURITY_JWT_SECRET_KEY=<<SECRET_KEY>>;
                SWAGGER_API_URL=http://IP:9933/api;
                ZIPKIN_URL=http://IP:9411/api/v2/spans
               ```

3. Rode a aplicação:
    - Clique no ícone `Run` no canto superior direito ou pressione `Shift + F10`.

## Utilizando Arquivos `.env`

Para gerenciar variáveis de ambiente usando arquivos `.env`, siga estas etapas:

1. Crie um arquivo `.env` na raiz do projeto:
    ```properties
    DATABASE_PASSWORD=<<PASSWORD>>!;
                DATABASE_URL=jdbc:postgresql://IP:PORTA/DATABASE_NAME;
                DATABASE_USERNAME=DATABASE_USERNAME;
                KAFKA_BOOTSTRAP_SERVERS=IP:9092;
                OTLP_URL=http://IP:4317;
                PAYMENT_PROVIDER=provider-local;
                SECURITY_JWT_SECRET_KEY=<<SECRET_KEY>>;
                SWAGGER_API_URL=http://IP:9933/api;
                ZIPKIN_URL=http://IP:9411/api/v2/spans
    ```

2. Adicione a dependência `spring-boot-dotenv` ao seu `pom.xml`:
    ```xml
    <dependency>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>java-dotenv</artifactId>
        <version>5.2.2</version>
    </dependency>
    ```

3. Configure sua aplicação para ler o arquivo `.env`. Adicione o seguinte código à classe principal ou a uma configuração:
    ```java
    import io.github.cdimascio.dotenv.Dotenv;

    @SpringBootApplication
    public class RunApplication {
        public static void main(String[] args) {
            Dotenv dotenv = Dotenv.configure().load();
            System.setProperty("DB_URL", dotenv.get("DB_URL"));
            System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
            System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
            SpringApplication.run(RunApplication.class, args);
        }
    }
    ```

4. Configure as propriedades do Spring Boot para usar as variáveis de ambiente:
    ```properties
    spring.datasource.url=${DB_URL}
    spring.datasource.username=${DB_USERNAME}
    spring.datasource.password=${DB_PASSWORD}
    ```

## Rodando PostgreSQL e SonarQube com Docker Compose

O projeto inclui um arquivo `docker-compose.yml` no pacote `infra/` para rodar o PostgreSQL e o SonarQube. Siga as instruções abaixo para configurar e rodar esses serviços:

1. Navegue até o diretório `infra`:
    ```bash
    cd infra/postgres
    # ou
    cd infra/sonar
    ```

2. Rode o Docker Compose:
    ```bash
    docker-compose up -d
    ```

3. Verifique se os serviços estão rodando:
    ```bash
    docker-compose ps
    ```

O PostgreSQL estará disponível na porta 5432 e o SonarQube na porta 9000.

## Configurando o Perfil de Produção

Para rodar o perfil de produção, adicione a opção `-Dspring.profiles.active=prod` ao rodar o Maven ou configurar no IntelliJ IDEA.

### Rodando com Maven

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```
Ambientes: prod,hom,dev

