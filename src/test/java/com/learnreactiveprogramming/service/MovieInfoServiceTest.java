package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.MovieInfo;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();
    MovieInfoService movieInfoService = new MovieInfoService(webClient);


    @Test
    void retrieveAllMovieInfo_RestClient() {

        Flux<MovieInfo> movieInfoFlux =
                movieInfoService.retrieveAllMovieInfo_RestClient();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(7)
                .verifyComplete();

    }

    @Test
    void retrieveMovieInfoById_RestClient() {


        Mono<MovieInfo> movieInfoMono =
                movieInfoService.retrieveMovieInfoById_RestClient(1L);

        StepVerifier.create(movieInfoMono)
                .assertNext(movie -> assertEquals("Batman Begins", movie.getName())
                )
                //.expectNextCount(1)
                .verifyComplete();
    }
}