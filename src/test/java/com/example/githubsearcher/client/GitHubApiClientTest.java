package com.example.githubsearcher.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.example.githubsearcher.dto.response.GitHubSearchResponse;
import com.example.githubsearcher.exception.GitHubApiException;

class GitHubApiClientTest {

    private GitHubApiClient gitHubApiClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {

        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.github.com");

        mockServer = MockRestServiceServer
                .bindTo(builder)
                .build();

        RestClient restClient = builder.build();

        gitHubApiClient = new GitHubApiClient(restClient);
    }
    
    @Test
    void searchRepositories_shouldReturnGitHubRepositories() {

        String responseBody = """
                {
                    "total_count": 1,
                    "incomplete_results": false,
                    "items": [
                        {
                            "id": 123,
                            "name": "spring-boot-demo",
                            "description": "A Spring Boot project",
                            "owner": {
                                "login": "john"
                            },
                            "language": "Java",
                            "stargazers_count": 500,
                            "forks_count": 100,
                            "updated_at": "2026-11-18T10:00:00Z"
                        }
                    ]
                }
                """;

        mockServer.expect(requestTo(
        	    "https://api.github.com/search/repositories?q=spring%20boot%20language:Java&sort=stars&order=desc&per_page=30"))
                .andRespond(withSuccess(
                        responseBody,
                        MediaType.APPLICATION_JSON
                ));

        GitHubSearchResponse result =
                gitHubApiClient.searchRepositories(
                        "spring boot", "Java", "stars"
                );

        assertNotNull(result);
        assertEquals(1, result.total_count());
        assertEquals("spring-boot-demo", result.items().get(0).name());
        assertEquals("john", result.items().get(0).owner().login());
        assertEquals(500, result.items().get(0).starsCount());

        mockServer.verify();
    }
    
    @Test
    void searchRepositories_shouldThrowExceptionWhenRateLimitExceeded() {

        mockServer.expect(requestTo(
                "https://api.github.com/search/repositories?q=spring&sort=stars&order=desc&per_page=30"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .header("X-RateLimit-Remaining", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                    "message": "API rate limit exceeded"
                                }
                                """));

        GitHubApiException exception = assertThrows(
                GitHubApiException.class,
                () -> gitHubApiClient.searchRepositories(
                        "spring", null, "stars"
                )
        );

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatus());

        mockServer.verify();
    }
    
    @Test
    void searchRepositories_shouldThrowExceptionWhenGitHubServerFails() {

        mockServer.expect(requestTo(
                "https://api.github.com/search/repositories?q=spring&sort=stars&order=desc&per_page=30"))
                .andRespond(withServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                    "message": "Internal Server Error"
                                }
                                """));

        GitHubApiException exception = assertThrows(
                GitHubApiException.class,
                () -> gitHubApiClient.searchRepositories(
                        "spring", null, "stars"
                )
        );

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());

        mockServer.verify();
    }
    
    @Test
    void searchRepositories_shouldThrowExceptionWhenGitHubReturnsClientError() {

        mockServer.expect(requestTo(
                "https://api.github.com/search/repositories?q=spring&sort=stars&order=desc&per_page=30"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        GitHubApiException exception = assertThrows(
                GitHubApiException.class,
                () -> gitHubApiClient.searchRepositories(
                        "spring", null, "stars"
                )
        );

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());

        mockServer.verify();
    }
    
    @Test
    void searchRepositories_shouldThrowExceptionWhenGitHubConnectionFails() {

        mockServer.expect(requestTo(
                "https://api.github.com/search/repositories?q=spring&sort=stars&order=desc&per_page=30"))
                .andRespond(request -> {
                    throw new ResourceAccessException("Connection timed out");
                });

        GitHubApiException exception = assertThrows(
                GitHubApiException.class,
                () -> gitHubApiClient.searchRepositories(
                        "spring", null, "stars"
                )
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());

        mockServer.verify();
    }
    
    @Test
    void searchRepositories_shouldReturnEmptyResults() {

        String responseBody = """
                {
                    "total_count": 0,
                    "incomplete_results": false,
                    "items": []
                }
                """;

        mockServer.expect(requestTo(
                "https://api.github.com/search/repositories?q=nonexistent&sort=stars&order=desc&per_page=30"))
                .andRespond(withSuccess(
                        responseBody,
                        MediaType.APPLICATION_JSON
                ));

        GitHubSearchResponse result =
                gitHubApiClient.searchRepositories(
                        "nonexistent", null, "stars"
                );

        assertNotNull(result);
        assertEquals(0, result.total_count());
        assertTrue(result.items().isEmpty());

        mockServer.verify();
    }

}