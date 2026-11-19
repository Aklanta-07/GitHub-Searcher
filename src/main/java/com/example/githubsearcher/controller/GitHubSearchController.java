
package com.example.githubsearcher.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.githubsearcher.dto.request.GitHubSearchRequest;
import com.example.githubsearcher.dto.response.ApiErrorResponse;
import com.example.githubsearcher.dto.response.GitHubRepoResponse;
import com.example.githubsearcher.service.GitHubSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
@Validated
@Tag(
	    name = "GitHub Repository Search",
	    description = "Search GitHub repositories and retrieve stored repository data"
	)
public class GitHubSearchController {

    private final GitHubSearchService gitHubSearchService;

    @PostMapping("/search")
    @Operation(
    	    summary = "Search GitHub repositories",
    	    description = """
    	        Searches GitHub repositories using a search query,
    	        optional programming language, and sorting preference.
    	        The returned repositories are stored or updated in PostgreSQL.
    	        """
    	)
    @ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "Repositories retrieved and stored successfully"),
    	    @ApiResponse(responseCode = "400", description = "Invalid search request", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
    	    @ApiResponse(responseCode = "429", description = "GitHub API rate limit exceeded", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
    	    @ApiResponse(responseCode = "502", description = "GitHub API request failed", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
    	    @ApiResponse(responseCode = "503", description = "GitHub API is unavailable", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<GitHubRepoResponse>> searchRepositories(
            @Valid @RequestBody GitHubSearchRequest request) {

        List<GitHubRepoResponse> repositories =
                gitHubSearchService.searchRepositories(request);

        return ResponseEntity.ok(repositories);
    }
    
    @GetMapping("/repositories")
    @Operation(
    	    summary = "Retrieve stored repositories",
    	    description = "Retrieves repositories from PostgreSQL with optional language, minimum stars, and sorting filters."
    	)
    @ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "Repositories retrieved successfully"),
    	    @ApiResponse(responseCode = "400", description = "Invalid filter or sorting parameter")
    })
    public ResponseEntity<List<GitHubRepoResponse>> getRepositories(
    		@Parameter(description = "Filter by programming language, for example Java") @RequestParam(required = false) String language,
    		@Parameter(description = "Minimum number of stars; must be zero or greater") @RequestParam(required = false) @Min(value = 0, message = "Minimum stars cannot be negative") Integer minStars,
    		@Parameter(description = "Sort by stars, forks, or updated. Defaults to stars.") @RequestParam(required = false, defaultValue = "stars") String sort
    		) {
    	
    	List<GitHubRepoResponse> repositories =
    			gitHubSearchService.getRepositories(language, minStars, sort);
    	
    	return ResponseEntity.ok(repositories);
    }
    
}
