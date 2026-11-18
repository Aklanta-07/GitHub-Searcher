package com.example.githubsearcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.githubsearcher.entity.GitHubRepo;

public interface GitHubRepository extends JpaRepository<GitHubRepo, Long> {

}
