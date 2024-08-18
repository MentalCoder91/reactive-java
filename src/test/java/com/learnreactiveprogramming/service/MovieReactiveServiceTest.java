package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {

    private final MovieInfoService movieInfoService = new MovieInfoService();

    private final ReviewService reviewService = new ReviewService();


    private final RevenueService revenueService = new RevenueService();

    MovieReactiveService movieReactiveService =
            new MovieReactiveService(movieInfoService, reviewService,revenueService);

    @Test
    void getAllMovies() {

        Flux<Movie> allMovies = movieReactiveService.getAllMovies();

        StepVerifier.create(allMovies)
                .assertNext(movie -> {

                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                })

                .assertNext(movie -> {

                    assertEquals("The Dark Knight", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .assertNext(movie->{

                    assertEquals("Dark Knight Rises",movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieById() {


        Mono<Movie> allMovies = movieReactiveService.getMovieById(1000L);

        StepVerifier.create(allMovies)
                .assertNext(movie -> {

                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieByIdUsingFlatMap() {

        Mono<Movie> allMovies = movieReactiveService.getMovieByIdUsingFlatMap(1000L);

        StepVerifier.create(allMovies)
                .assertNext(movie -> {

                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }


    @Test
    void getMovieByIdWithRevenue() {



        Mono<Movie> allMovies = movieReactiveService.getMovieByIdWithRevenue(1000L);

        StepVerifier.create(allMovies)
                .assertNext(movie -> {

                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                    assertEquals(1000000L,movie.getRevenue().getBudget());
                })
                .verifyComplete();

    }


}