package com.example.githubsearcher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.githubsearcher.client.GitHubApiClient;
import com.example.githubsearcher.dto.request.GitHubSearchRequest;
import com.example.githubsearcher.dto.response.GitHubApiRepoResponse;
import com.example.githubsearcher.dto.response.GitHubRepoResponse;
import com.example.githubsearcher.dto.response.GitHubSearchResponse;
import com.example.githubsearcher.entity.GitHubRepo;
import com.example.githubsearcher.repository.GitHubRepository;

@ExtendWith(MockitoExtension.class)
class GitHubSearchServiceTest {

    @Mock
    private GitHubApiClient gitHubApiClient;

    @Mock
    private GitHubRepository gitHubRepository;

    @InjectMocks
    private GitHubSearchService gitHubSearchService;

    @BeforeEach
    void setUp() {
        // Mockito injects the mocked dependencies.
    }
    
    @Test
    void searchRepositories_shouldDefaultSortToStars() {

        GitHubSearchRequest request =
                new GitHubSearchRequest("spring boot", "Java", null);

        when(gitHubApiClient.searchRepositories(
                "spring boot", "Java", "stars"))
                .thenReturn(new GitHubSearchResponse(
                        0, false, List.of()
                ));

        List<GitHubRepoResponse> result =
                gitHubSearchService.searchRepositories(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(gitHubApiClient).searchRepositories(
                "spring boot", "Java", "stars"
        );

        verifyNoInteractions(gitHubRepository);
    }
    
    @Test
    void searchRepositories_shouldSaveRepositoriesUsingUpsert() {

        GitHubApiRepoResponse apiRepo = new GitHubApiRepoResponse(
                123L,
                "spring-boot-demo",
                "A Spring Boot project",
                new GitHubApiRepoResponse.Owner("john"),
                "Java",
                500,
                100,
                Instant.parse("2026-11-18T10:00:00Z")
        );

        when(gitHubApiClient.searchRepositories(
                "spring boot", "Java", "stars"))
                .thenReturn(new GitHubSearchResponse(
                        1, false, List.of(apiRepo)
                ));

        GitHubSearchRequest request =
                new GitHubSearchRequest("spring boot", "Java", null);

        List<GitHubRepoResponse> result =
                gitHubSearchService.searchRepositories(request);

        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).id());
        assertEquals("spring-boot-demo", result.get(0).name());
        assertEquals("john", result.get(0).ownerName());

        verify(gitHubRepository).upsert(
                eq(123L),
                eq("spring-boot-demo"),
                eq("A Spring Boot project"),
                eq("john"),
                eq("Java"),
                eq(500),
                eq(100),
                eq(Instant.parse("2026-11-18T10:00:00Z"))
        );
    }
    
    @Test
    void getRepositories_shouldApplyFiltersAndSort() {

        GitHubRepo repo = new GitHubRepo();
        repo.setId(123L);
        repo.setName("spring-boot-demo");
        repo.setDescription("A Spring Boot project");
        repo.setOwnerName("john");
        repo.setLanguage("Java");
        repo.setStarsCount(500);
        repo.setForksCount(100);
        repo.setLastUpdatedAt(
                Instant.parse("2026-11-18T10:00:00Z")
        );

        when(gitHubRepository.findByFilters(
                "Java",
                100,
                Sort.by(Sort.Direction.DESC, "starsCount")
        )).thenReturn(List.of(repo));

        List<GitHubRepoResponse> result =
                gitHubSearchService.getRepositories(
                        "Java", 100, "stars"
                );

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).language());
        assertEquals(500, result.get(0).starsCount());

        verify(gitHubRepository).findByFilters(
                eq("Java"),
                eq(100),
                eq(Sort.by(Sort.Direction.DESC, "starsCount"))
        );
    }
    
    @Test
    void getRepositories_shouldThrowExceptionForInvalidSort() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gitHubSearchService.getRepositories(
                        "Java", 100, "invalid"
                )
        );

        assertEquals(
                "Invalid sort value. Allowed values: stars, forks, updated",
                exception.getMessage()
        );

        verifyNoInteractions(gitHubRepository);
    }
    
    @Test
    void searchRepositories_shouldReturnEmptyListWhenNoResultsFound() {

        GitHubSearchRequest request =
                new GitHubSearchRequest("nonexistent-project-xyz", "Java", null);

        when(gitHubApiClient.searchRepositories(
                "nonexistent-project-xyz", "Java", "stars"))
                .thenReturn(new GitHubSearchResponse(
                        0, false, List.of()
                ));

        List<GitHubRepoResponse> result =
                gitHubSearchService.searchRepositories(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(gitHubApiClient).searchRepositories(
                "nonexistent-project-xyz", "Java", "stars"
        );

        verifyNoInteractions(gitHubRepository);
    }

}