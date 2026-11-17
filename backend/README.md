# Bibliotech

Aplicação de avaliação com backend Java + Spring Boot e front-end estático em HTML/JavaScript.

## Requisitos

- JDK 17
- Maven 3.9+
- MySQL 5.7 ou superior

## Como executar

1. Configure o Java 17:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot"
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   ```

2. Crie o banco e a tabela executando `database.sql` no MySQL Workbench.

3. Inicie o backend no IntelliJ ou no terminal:
   ```powershell
   cd backend
   mvn package -DskipTests
   java -jar target\bibliotech-0.0.1-SNAPSHOT.jar
   ```
   Configure `DB_USERNAME` e `DB_PASSWORD` como variáveis de ambiente do Windows. Se o IntelliJ já estava aberto quando elas foram configuradas, reinicie-o para que o processo do backend receba as variáveis. Não é necessário armazenar a senha em arquivos do projeto.

4. Abra o front-end em um navegador:
   - basta abrir a pasta frontend/index.html ou servir a pasta com um HTTP server.

## Endpoints da API

- GET /api/livros
- GET /api/livros/{id}
- POST /api/livros
- PUT /api/livros/{id}
- DELETE /api/livros/{id}

## Banco de dados

O MySQL é o banco padrão da aplicação. O script `database.sql` cria o banco e a tabela
`livros`; o JPA mantém a estrutura atualizada ao iniciar. Os testes automatizados usam
H2 isoladamente pelo perfil `test`, sem alterar nem depender do banco MySQL local.
