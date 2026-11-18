package com.example.githubsearcher.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubSearchResponse(
    Integer total_count,
    Boolean incomplete_results,
    List<GitHubApiRepoResponse> items
) {
}
