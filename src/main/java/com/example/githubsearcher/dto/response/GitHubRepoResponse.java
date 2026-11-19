
package com.example.githubsearcher.dto.response;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "GitHub repository information stored in the database")
public record GitHubRepoResponse(

    @Schema(description = "Unique GitHub repository ID", example = "123456789")
    Long id,

    @Schema(description = "Repository name", example = "spring-boot")
    String name,

    @Schema(description = "Repository description", example = "Spring Boot framework")
    String description,

    @Schema(description = "GitHub repository owner's username", example = "spring-projects")
    String ownerName,

    @Schema(description = "Primary programming language", example = "Java")
    String language,

    @Schema(description = "Number of stars", example = "75000")
    Integer starsCount,

    @Schema(description = "Number of forks", example = "40000")
    Integer forksCount,

    @Schema(
        description = "Date and time when the repository was last updated",
        example = "2026-11-18T10:30:00Z"
    )
    Instant lastUpdatedAt

) {}
