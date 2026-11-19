package com.example.githubsearcher.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.githubsearcher.entity.GitHubRepo;

public interface GitHubRepository extends JpaRepository<GitHubRepo, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO github_repositories (
                id, name, description, owner_name, language,
                stars_count, forks_count, last_updated_at
            )
            VALUES (
                :id, :name, :description, :ownerName, :language,
                :starsCount, :forksCount, :lastUpdatedAt
            )
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                description = EXCLUDED.description,
                owner_name = EXCLUDED.owner_name,
                language = EXCLUDED.language,
                stars_count = EXCLUDED.stars_count,
                forks_count = EXCLUDED.forks_count,
                last_updated_at = EXCLUDED.last_updated_at
            """, nativeQuery = true)
    void upsert(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("description") String description,
            @Param("ownerName") String ownerName,
            @Param("language") String language,
            @Param("starsCount") Integer starsCount,
            @Param("forksCount") Integer forksCount,
            @Param("lastUpdatedAt") Instant lastUpdatedAt
    );
    
    @Query("""
            SELECT r FROM GitHubRepo r
            WHERE (:language IS NULL OR r.language = :language)
            AND (:minStars IS NULL OR r.starsCount >= :minStars)
            """)
    List<GitHubRepo> findByFilters(
            @Param("language") String language,
            @Param("minStars") Integer minStars,
            Sort sort
    );
    
}
