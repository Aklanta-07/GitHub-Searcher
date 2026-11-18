package com.example.githubsearcher.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GitHubSearchRequest(

    @NotBlank(message = "Search query must not be blank")
    String query,

    String language,

    @Pattern(
        regexp = "stars|forks|updated",
        message = "Sort must be stars, forks, or updated"
    )
    String sort

) {
}
