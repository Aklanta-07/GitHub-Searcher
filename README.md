# GitHub Repository Searcher

A Spring Boot REST API that searches GitHub repositories using the GitHub Search API, stores repository information in PostgreSQL, and provides endpoints to retrieve stored repositories with filtering and sorting.

## Features

- Search GitHub repositories using keywords.
- Filter GitHub search results by programming language.
- Sort GitHub search results by stars, forks, or last updated date.
- Persist repository information in PostgreSQL.
- Prevent duplicate records using GitHub repository IDs and PostgreSQL UPSERT.
- Retrieve stored repositories with optional language and minimum-star filters.
- Validate incoming requests and query parameters.
- Centralized exception handling with consistent JSON error responses.
- Interactive API documentation using Swagger UI.
- Automated unit and controller tests.

## Technology Stack

| Technology        | Purpose                         |
| ----------------- | ------------------------------- |
| Java 21           | Programming language            |
| Spring Boot       | Application framework           |
| Spring Web        | REST API development            |
| Spring Data JPA   | Database persistence            |
| PostgreSQL        | Relational database             |
| RestClient        | GitHub API communication        |
| Maven             | Build and dependency management |
| JUnit 5           | Testing                         |
| Mockito           | Mocking dependencies            |
| Springdoc OpenAPI | API documentation               |
| Docker            | PostgreSQL containerization     |

## Project Structure

```text
src/main/java/com/example/githubsearcher/
├── client/
├── config/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── exception/
├── repository/
└── service/
```

## Prerequisites

- Java 21
- Maven
- Docker Desktop
- Git

## Database Setup

Start PostgreSQL using Docker:

```bash
docker run --name github-searcher-db \
  -e POSTGRES_DB=github_searcher \
  -e POSTGRES_USER=github_user \
  -e POSTGRES_PASSWORD=github_pass \
  -p 5432:5432 \
  -d postgres:17
```

The application connects to PostgreSQL on port `5432`.

## Configuration

Configure the database connection in `src/main/resources/application.properties`:

```properties
spring.application.name=github-searcher

spring.datasource.url=jdbc:postgresql://localhost:5432/github_searcher
spring.datasource.username=github_user
spring.datasource.password=github_pass
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

server.port=8080
```

For production deployment, use environment variables for database credentials and use an appropriate schema migration strategy instead of relying on `ddl-auto=update`.

## Running the Application

Clone the repository:

```bash
git clone https://github.com/Aklanta-07/GitHub-Searcher.git
cd github-searcher
```

Build and test:

```bash
mvn clean test
```

Run the application:

```bash
mvn spring-boot:run
```

The application runs at:

```text
http://localhost:8080
```

## API Endpoints

### 1. Search GitHub Repositories

**POST** `/api/github/search`

Searches GitHub repositories and saves the returned repository information in PostgreSQL.

Example request:

```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

Supported sort values:

- `stars`
- `forks`
- `updated`

### 2. Retrieve Stored Repositories

**GET** `/api/github/repositories`

Retrieves repositories previously stored in PostgreSQL.

Supported query parameters:

| Parameter  | Description                           | Example |
| ---------- | ------------------------------------- | ------- |
| `language` | Filter by programming language        | `Java`  |
| `minStars` | Minimum number of stars               | `100`   |
| `sort`     | Sort by stars, forks, or updated date | `stars` |

Example:

```http
GET /api/github/repositories?language=Java&minStars=100&sort=stars
```

The default sorting order is descending by stars.

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Error Handling

The application provides centralized exception handling and a consistent error response structure.

Example:

```json
{
  "error": "Invalid value for parameter: minStars"
}
```

Commonly handled scenarios include:

- Invalid request fields.
- Invalid query parameter values.
- Malformed JSON request bodies.
- GitHub API rate limits.
- GitHub API failures.
- External service connectivity failures.

## Testing

Run the automated test suite:

```bash
mvn clean test
```

The test suite covers API client behavior, service logic, controller behavior, and application context initialization.

## Persistence Strategy

GitHub repository IDs are used as unique identifiers.

PostgreSQL UPSERT (`ON CONFLICT DO UPDATE`) ensures that existing repository records are updated instead of creating duplicates when the same repository appears in subsequent searches.

## Author

**Aklanta Swain**
