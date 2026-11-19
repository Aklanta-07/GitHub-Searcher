package com.example.githubsearcher.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

import com.example.githubsearcher.exception.GitHubApiException;

import com.example.githubsearcher.dto.response.GitHubSearchResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GitHubApiClient {

    private final RestClient gitHubRestClient;
    
    public GitHubSearchResponse searchRepositories(
            String query,
            String language,
            String sort) {

        String githubQuery = (language != null && !language.isBlank())
                ? query + " language:" + language
                : query;

        try {
            return gitHubRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/repositories")
                            .queryParam("q", githubQuery)
                            .queryParam("sort", sort)
                            .queryParam("order", "desc")
                            .queryParam("per_page", 30)
                            .build())
                    .retrieve()
                    .body(GitHubSearchResponse.class);

        } catch (RestClientResponseException ex) {

            boolean rateLimitExceeded =
                    ex.getStatusCode().value() == 429
                    || (ex.getStatusCode().value() == 403
                        && ("0".equals(ex.getResponseHeaders()
                                .getFirst("X-RateLimit-Remaining"))
                            || ex.getResponseBodyAsString()
                                .toLowerCase()
                                .contains("rate limit")));

            if (rateLimitExceeded) {
                throw new GitHubApiException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "GitHub API rate limit exceeded. Please try again later."
                );
            }

            if (ex.getStatusCode().is5xxServerError()) {
                throw new GitHubApiException(
                        HttpStatus.BAD_GATEWAY,
                        "GitHub API is currently unavailable."
                );
            }

            throw new GitHubApiException(
                    HttpStatus.BAD_GATEWAY,
                    "GitHub API request failed."
            );

        } catch (ResourceAccessException ex) {

            throw new GitHubApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to connect to GitHub API. Please try again later."
            );
        }
    }

}