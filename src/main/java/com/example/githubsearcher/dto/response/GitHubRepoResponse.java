package com.example.githubsearcher.dto.response;

import java.time.Instant;

public record GitHubRepoResponse(

    Long id,

    String name,

    String description,

    String ownerName,

    String language,

    Integer starsCount,

    Integer forksCount,

    Instant lastUpdatedAt

) {
}
