package com.example.githubsearcher.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API error response")
public record ApiErrorResponse(

    @Schema(
        description = "Description of the error",
        example = "Search query must not be blank"
    )
    String error

) {}