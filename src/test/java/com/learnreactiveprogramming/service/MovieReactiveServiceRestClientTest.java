package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceRestClientTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();
    private MovieInfoService movieInfoService = new MovieInfoService(webClient);

    private ReviewService reviewService = new ReviewService(webClient);

    private MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService,reviewService);

    @Test
    void getAllMovies_RestClient() {

        Flux<Movie> allMoviesRestClient =
                movieReactiveService.getAllMovies_RestClient();

        StepVerifier.create(allMoviesRestClient)
                .expectNextCount(7)
                .verifyComplete();



    }


    @Test
    void getMovieById_RestClient() {

        Mono<Movie> movieByIdRestClient = movieReactiveService.getMovieById_RestClient(1L);

        StepVerifier.create(movieByIdRestClient)
                //.expectNextCount(1)
                .assertNext(movie->{
                    assertEquals("Batman Begins", movie.getMovie().getName());
                })
                .verifyComplete();

    }
}