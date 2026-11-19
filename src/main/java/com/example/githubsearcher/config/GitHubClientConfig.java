package com.example.githubsearcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import java.time.Duration;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class GitHubClientConfig {
	
	@Bean
	public RestClient gitHubRestClient() {

	    SimpleClientHttpRequestFactory requestFactory =
	            new SimpleClientHttpRequestFactory();

	    requestFactory.setConnectTimeout(Duration.ofSeconds(5));
	    requestFactory.setReadTimeout(Duration.ofSeconds(10));

	    return RestClient.builder()
	            .baseUrl("https://api.github.com")
	            .requestFactory(requestFactory)
	            .defaultHeader("Accept", "application/vnd.github+json")
	            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
	            .build();
	}

}
