package com.example.githubsearcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	    info = @Info(
	        title = "GitHub Repository Searcher API",
	        version = "1.0.0",
	        description = """
	            REST API for searching GitHub repositories,
	            storing repository information in PostgreSQL,
	            and retrieving stored repositories with filters.
	            """,
	        contact = @Contact(
	            name = "GitHub Searcher"
	        )
	    )
	)
@SpringBootApplication
public class GithubSearcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubSearcherApplication.class, args);
	}

}
