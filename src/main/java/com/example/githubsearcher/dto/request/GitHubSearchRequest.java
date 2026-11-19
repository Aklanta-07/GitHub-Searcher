package com.example.githubsearcher.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

public record GitHubSearchRequest(

    @Schema(
        description = "Repository name or search terms",
        example = "spring boot",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Search query must not be blank")
    String query,

    @Schema(
        description = "Filter repositories by programming language",
        example = "Java"
    )
    String language,

    @Schema(
        description = "Sort GitHub search results",
        example = "stars",
        allowableValues = {"stars", "forks", "updated"},
        defaultValue = "stars"
    )
    @Pattern(
        regexp = "stars|forks|updated",
        message = "Sort must be stars, forks, or updated"
    )
    String sort

) {}
