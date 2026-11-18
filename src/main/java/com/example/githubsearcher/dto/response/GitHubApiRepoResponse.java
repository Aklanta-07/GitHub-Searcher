package com.example.githubsearcher.dto.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubApiRepoResponse(

    Long id,

    String name,

    String description,

    Owner owner,

    String language,

    @JsonProperty("stargazers_count")
    Integer starsCount,

    @JsonProperty("forks_count")
    Integer forksCount,

    @JsonProperty("updated_at")
    Instant lastUpdatedAt

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(String login) {
    }
}
