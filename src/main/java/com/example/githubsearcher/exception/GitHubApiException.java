package com.example.githubsearcher.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class GitHubApiException extends RuntimeException {

    private final HttpStatus status;

    public GitHubApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}