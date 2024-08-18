package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.exception.NetworkException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MovieReactiveServiceMockitoTest {

    @Mock
    private MovieInfoService movieInfoService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private MovieReactiveService service;

    @Test
    void getAllMovies() {


        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();


        Flux<Movie> allMovies = service.getAllMovies();


        StepVerifier.create(allMovies)
                .expectNextCount(3)
                .verifyComplete();


    }

    @Test
    void getAllMovies_1() {


        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException("Reviews Error"));


        Flux<Movie> allMovies = service.getAllMovies();


        StepVerifier.create(allMovies)
                .expectErrorMessage("Reviews Error")
                .verify();


    }

    @Test
    void getAllMovies_retry() {


        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException("Reviews Error"));


        Flux<Movie> allMovies = service.getAllMovies_retry();


        StepVerifier.create(allMovies)
                .expectErrorMessage("Reviews Error")
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMovies_retryWhen() {


        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException("Reviews Error"));


        Flux<Movie> allMovies = service.getAllMovies_retryWhen();


        StepVerifier.create(allMovies)
                .expectErrorMessage("Reviews Error")
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_retryWhen_1() {


        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();


        //Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException("Reviews Error"));

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new NetworkException("Reviews Error"));// retry in case of only NetworkException


        Flux<Movie> allMovies = service.getAllMovies_retryWhen();


        StepVerifier.create(allMovies)
                .expectErrorMessage("Reviews Error")
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }


    @Test
    void getAllMovies_repeat() {


        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();


        Flux<Movie> allMovies = service.getAllMovies_repeat();


        StepVerifier.create(allMovies)
                .expectNextCount(6)
                .thenCancel()
                .verify();

        verify(reviewService, times(6)).retrieveReviewsFlux(isA(Long.class));
    }


    @Test
    void getAllMovies_repeat_n() {


        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        var n = 2L;
        Flux<Movie> allMovies = service.getAllMovies_repeat(n);


        StepVerifier.create(allMovies)
                .expectNextCount(9)
                .verifyComplete();

        verify(reviewService, times(9)).retrieveReviewsFlux(isA(Long.class));
    }
}
