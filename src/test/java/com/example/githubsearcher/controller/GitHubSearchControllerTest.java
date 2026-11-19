package com.example.githubsearcher.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.githubsearcher.dto.request.GitHubSearchRequest;
import com.example.githubsearcher.dto.response.GitHubRepoResponse;
import com.example.githubsearcher.service.GitHubSearchService;

@WebMvcTest(GitHubSearchController.class)
class GitHubSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubSearchService gitHubSearchService;
    
    @Test
    void searchRepositories_shouldReturn200AndRepositories() throws Exception {

        GitHubRepoResponse response = new GitHubRepoResponse(
                123L,
                "spring-boot-demo",
                "A Spring Boot project",
                "john",
                "Java",
                500,
                100,
                Instant.parse("2026-11-18T10:00:00Z")
        );

        when(gitHubSearchService.searchRepositories(
                any(GitHubSearchRequest.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/api/github/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "query": "spring boot",
                            "language": "Java",
                            "sort": "stars"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(123))
                .andExpect(jsonPath("$[0].name").value("spring-boot-demo"))
                .andExpect(jsonPath("$[0].language").value("Java"));
    }
    
    @Test
    void searchRepositories_shouldReturn400WhenQueryIsBlank() throws Exception {

        mockMvc.perform(post("/api/github/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "query": "",
                            "language": "Java",
                            "sort": "stars"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Search query must not be blank"));

        verifyNoInteractions(gitHubSearchService);
    }
    
    @Test
    void getRepositories_shouldReturn200AndRepositories() throws Exception {

        GitHubRepoResponse response = new GitHubRepoResponse(
                123L,
                "spring-boot-demo",
                "A Spring Boot project",
                "john",
                "Java",
                500,
                100,
                Instant.parse("2026-11-18T10:00:00Z")
        );

        when(gitHubSearchService.getRepositories(
                "Java", 100, "stars"))
                .thenReturn(List.of(response));

        mockMvc.perform(get(
                "/api/github/repositories?language=Java&minStars=100&sort=stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(123))
                .andExpect(jsonPath("$[0].name").value("spring-boot-demo"))
                .andExpect(jsonPath("$[0].starsCount").value(500))
                .andExpect(jsonPath("$[0].language").value("Java"));

        verify(gitHubSearchService).getRepositories(
                "Java", 100, "stars"
        );
    }
    
    @Test
    void getRepositories_shouldReturn400WhenMinStarsIsNegative() throws Exception {

        mockMvc.perform(get(
                "/api/github/repositories?minStars=-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Minimum stars cannot be negative"));

        verifyNoInteractions(gitHubSearchService);
    }
    
    @Test
    void getRepositories_shouldReturn400WhenMinStarsIsNotNumeric() throws Exception {

        mockMvc.perform(get("/api/github/repositories")
                        .param("minStars", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid value for parameter: minStars"));
    }

}