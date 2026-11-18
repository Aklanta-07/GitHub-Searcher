
package com.example.githubsearcher.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "github_repositories")
@Getter
@Setter
public class GitHubRepo {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    private String language;

    @Column(name = "stars_count", nullable = false)
    private Integer starsCount;

    @Column(name = "forks_count", nullable = false)
    private Integer forksCount;

    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;
}
