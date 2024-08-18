package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Revenue;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {


    private MovieInfoService movieInfoService;

    private ReviewService reviewService;

    private RevenueService revenueService;


    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies() {

        var movieInfoFlux = movieInfoService.retrieveMoviesFlux().log();

        return movieInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));

                })
                .onErrorMap(ex -> {
                    log.error("Something went wrong :" + ex.getMessage());
                    throw new MovieException(ex.getMessage());
                })
                .log();


    }

    public Flux<Movie> getAllMovies_RestClient() {

        var movieInfoFlux = movieInfoService.retrieveAllMovieInfo_RestClient().log();

        return movieInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux_RestClient(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));

                })
                .onErrorMap(ex -> {
                    log.error("Something went wrong :" + ex.getMessage());
                    throw new MovieException(ex.getMessage());
                })
                .log();


    }



    public Flux<Movie> getAllMovies_retry() {

        var movieInfoFlux = movieInfoService.retrieveMoviesFlux().log();

        return movieInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));

                })
                .onErrorMap(ex -> {
                    log.error("Something went wrong :" + ex.getMessage());
                    throw new MovieException(ex.getMessage());
                })
                .retry(3)
                .log();


    }

    public Flux<Movie> getAllMovies_retryWhen() {


//        RetryBackoffSpec backoff = Retry.backoff(3, Duration.ofMillis(2000)).onRetryExhaustedThrow((retryBackoff,retrySignal)->
//                        Exceptions.propagate(retrySignal.failure())
//                );


        var movieInfoFlux = movieInfoService.retrieveMoviesFlux().log();

        return movieInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));

                })
                .onErrorMap(ex -> {
                    log.error("Something went wrong :" + ex.getMessage());
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .log();


    }


    public Flux<Movie> getAllMovies_repeat() {


        var movieInfoFlux = movieInfoService.retrieveMoviesFlux().log();

        return movieInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));

                })
                .onErrorMap(ex -> {
                    log.error("Something went wrong :" + ex.getMessage());
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .repeat()
                .log();


    }


    public Flux<Movie> getAllMovies_repeat(long n) {


        var movieInfoFlux = movieInfoService.retrieveMoviesFlux().log();

        return movieInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));

                })
                .onErrorMap(ex -> {
                    log.error("Something went wrong :" + ex.getMessage());
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .repeat(n)
                .log();


    }

    private static RetryBackoffSpec getRetryBackoffSpec() {
        return Retry.backoff(3, Duration.ofMillis(2000)).onRetryExhaustedThrow((retryBackoff, retrySignal) ->
                Exceptions.propagate(retrySignal.failure())
        ).filter(throwable -> throwable instanceof MovieException);

    }


    public Mono<Movie> getMovieById(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(1000L).log();

        var reviewFlux = reviewService.retrieveReviewsFlux(movieId).collectList();

        return movieInfoMono.zipWith(reviewFlux, (movieInfo, reviewsFlux) -> new Movie(movieInfo, reviewsFlux));
    }


    public Mono<Movie> getMovieById_RestClient(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoById_RestClient(movieId).log();

        var reviewFlux = reviewService.retrieveReviewsFlux_RestClient(movieId).collectList();

        return movieInfoMono.zipWith(reviewFlux, (movieInfo, reviewsFlux) -> new Movie(movieInfo, reviewsFlux));
    }

    public Mono<Movie> getMovieByIdUsingFlatMap(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(1000L).log();


        return movieInfoMono.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
            return reviewsMono.map(reviewsList -> new Movie(movieInfo, reviewsList));
        });
    }


    public Mono<Movie> getMovieByIdWithRevenue(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(1000L).log();

        var reviewFlux = reviewService.retrieveReviewsFlux(movieId).collectList();


        Mono<Revenue> revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic()).log();


        return movieInfoMono
                .zipWith(reviewFlux, (movieInfo, reviewsFlux) -> new Movie(movieInfo, reviewsFlux))
                .zipWith(revenueMono,(movie,revenue)->{

                     movie.setRevenue(revenue);
                    log.info("Movie :{}",movie);
                     return movie;
                });
    }
}
