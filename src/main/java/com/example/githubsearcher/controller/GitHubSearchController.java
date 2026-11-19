
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
import com.example.githubsearcher.dto.response.GitHubRepoResponse;
import com.example.githubsearcher.service.GitHubSearchService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
@Validated
public class GitHubSearchController {

    private final GitHubSearchService gitHubSearchService;

    @PostMapping("/search")
    public ResponseEntity<List<GitHubRepoResponse>> searchRepositories(
            @Valid @RequestBody GitHubSearchRequest request) {

        List<GitHubRepoResponse> repositories =
                gitHubSearchService.searchRepositories(request);

        return ResponseEntity.ok(repositories);
    }
    
    @GetMapping("/repositories")
    public ResponseEntity<List<GitHubRepoResponse>> getRepositories(
    		@RequestParam(required = false) String language,
    		@RequestParam(required = false) @Min(value = 0, message = "Minimum stars cannot be negative") Integer minStars,
    		@RequestParam(required = false, defaultValue = "stars") String sort
    		) {
    	
    	List<GitHubRepoResponse> repositories =
    			gitHubSearchService.getRepositories(language, minStars, sort);
    	
    	return ResponseEntity.ok(repositories);
    }
    
}
