package com.example.resttemplate.service;

import com.example.resttemplate.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PostClientService {

    private static final Logger log = LoggerFactory.getLogger(PostClientService.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PostClientService(RestTemplate restTemplate,
                             @Value("${external.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Post getPostById(Long id) {
        String url = baseUrl + "/posts/{id}";
        log.debug("GET {}", url.replace("{id}", String.valueOf(id)));

        try {
            return restTemplate.getForObject(url, Post.class, id);
        } catch (RestClientException e) {
            log.error("Failed to get post {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public List<Post> getPosts(Long userId) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/posts");

        if (userId != null) {
            builder.queryParam("userId", userId);
        }

        String url = builder.toUriString();
        log.debug("GET {}", url);

        try {
            ResponseEntity<Post[]> response = restTemplate.getForEntity(url, Post[].class);
            Post[] body = response.getBody();
            return body != null ? Arrays.asList(body) : Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Failed to get posts: {}", e.getMessage());
            throw e;
        }
    }

    public Post createPost(Post post) {
        String url = baseUrl + "/posts";
        log.debug("POST {} with body: {}", url, post);

        try {
            return restTemplate.postForObject(url, post, Post.class);
        } catch (RestClientException e) {
            log.error("Failed to create post: {}", e.getMessage());
            throw e;
        }
    }

    public Post updatePost(Long id, Post post) {
        String url = baseUrl + "/posts/{id}";
        log.debug("PUT {} with body: {}", url.replace("{id}", String.valueOf(id)), post);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Post> entity = new HttpEntity<>(post, headers);

        try {
            ResponseEntity<Post> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    Post.class,
                    id
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Failed to update post {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public void deletePost(Long id) {
        String url = baseUrl + "/posts/{id}";
        log.debug("DELETE {}", url.replace("{id}", String.valueOf(id)));

        try {
            restTemplate.delete(url, id);
        } catch (RestClientException e) {
            log.error("Failed to delete post {}: {}", id, e.getMessage());
            throw e;
        }
    }
}
