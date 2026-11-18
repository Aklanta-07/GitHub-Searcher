package com.example.githubsearcher.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.githubsearcher.dto.response.GitHubSearchResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GitHubApiClient {

    private final RestClient gitHubRestClient;
    
	public GitHubSearchResponse searchRepositories(
			String query,
			String language,
			String sort
			) {
		
		String githubQuery = (language != null && !language.isBlank())
	            ? query + " language:" + language
	            : query;
		
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
	}

}