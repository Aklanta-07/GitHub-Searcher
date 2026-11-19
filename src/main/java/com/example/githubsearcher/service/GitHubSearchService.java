package com.example.githubsearcher.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.example.githubsearcher.client.GitHubApiClient;
import com.example.githubsearcher.dto.request.GitHubSearchRequest;
import com.example.githubsearcher.dto.response.GitHubApiRepoResponse;
import com.example.githubsearcher.dto.response.GitHubRepoResponse;
import com.example.githubsearcher.dto.response.GitHubSearchResponse;
import com.example.githubsearcher.entity.GitHubRepo;
import com.example.githubsearcher.repository.GitHubRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitHubSearchService {
	
	private final GitHubApiClient gitHubApiClient;
	private final GitHubRepository gitHubRepository;
	
	public List<GitHubRepoResponse>  searchRepositories(GitHubSearchRequest request) {
		
		String sort = (request.sort() == null || request.sort().isBlank())
	            ? "stars"
	            : request.sort();
		
		GitHubSearchResponse searchResponse =
	            gitHubApiClient.searchRepositories(
	                    request.query(),
	                    request.language(),
	                    sort
	            );
		
		if (searchResponse == null || searchResponse.items() == null) {
	        return List.of();
	    }
		
		List<GitHubRepo> repositories = searchResponse.items()
	            .stream()
	            .map(this::mapToEntity)
	            .toList();

		repositories.forEach(repo ->
			        gitHubRepository.upsert(
			                repo.getId(),
			                repo.getName(),
			                repo.getDescription(),
			                repo.getOwnerName(),
			                repo.getLanguage(),
			                repo.getStarsCount(),
			                repo.getForksCount(),
			                repo.getLastUpdatedAt()
			        )
     	);

	    return repositories.stream()
	            .map(this::mapToResponse)
	            .toList();
	}
	
	public List<GitHubRepoResponse> getRepositories(
			String language,
			Integer minStars,
			String sort
			) {
		
		String sortField = switch (
				sort == null || sort.isBlank() ? "stars" : sort.toLowerCase()
				) {
				case "stars" -> "starsCount";
		        case "forks" -> "forksCount";
		        case "updated" -> "lastUpdatedAt";
		        default -> throw new IllegalArgumentException(
		                "Invalid sort value. Allowed values: stars, forks, updated"
		        );
		};
		
		List<GitHubRepo> repositories = gitHubRepository.findByFilters(
				language == null || language.isBlank() ? null : language.trim(),
				minStars,
				Sort.by(Direction.DESC, sortField)
				);
		
		return repositories.stream()
				.map(this::mapToResponse)
				.toList();
		
	}
	
	private GitHubRepo mapToEntity(GitHubApiRepoResponse apiRepo) {

	    GitHubRepo repo = new GitHubRepo();

	    repo.setId(apiRepo.id());
	    repo.setName(apiRepo.name());
	    repo.setDescription(apiRepo.description());
	    repo.setOwnerName(apiRepo.owner() != null ? apiRepo.owner().login() : "unknown");
	    repo.setLanguage(apiRepo.language());
	    repo.setStarsCount(apiRepo.starsCount());
	    repo.setForksCount(apiRepo.forksCount());
	    repo.setLastUpdatedAt(apiRepo.lastUpdatedAt());

	    return repo;
	}
	
	private GitHubRepoResponse mapToResponse(GitHubRepo repo) {

	    return new GitHubRepoResponse(
	            repo.getId(),
	            repo.getName(),
	            repo.getDescription(),
	            repo.getOwnerName(),
	            repo.getLanguage(),
	            repo.getStarsCount(),
	            repo.getForksCount(),
	            repo.getLastUpdatedAt()
	    );
	}

}
